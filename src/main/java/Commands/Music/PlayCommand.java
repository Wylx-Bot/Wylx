package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayCommand extends ServerCommand {
    private static final int MAX_SEARCH_TRACKS = 5;
    // Find &t=<nums>, but don't capture &t=
    private static final Pattern ytTimestamp = Pattern.compile("(?<=&t=)[0-9]*");

    public PlayCommand() {
        super("play",
                CommandPermission.EVERYONE,
                """
                        Play or queue a song to play
                        Usage:
                        %{p}play <link> <Optional: seconds to skip OR HH:MM:SS>
                        %{p}play <search terms>""",
                "p");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        var playerManager = WylxPlayerManager.getInstance();
        String[] args = ctx.args();
        boolean isSearch;

        if (args.length < 2) {
            displayUsage(event.getChannel());
            return;
        }

        if (!MusicUtils.canUseVoiceCommand(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        isSearch = !args[1].contains(".") || !args[1].contains("/");

        String token;
        // Get start location if user gives time
        Duration dur = Duration.ofSeconds(0);

        if (isSearch) {
            token = "ytsearch:" + event.getMessage().getContentRaw().substring(args[0].length() + 1);
        } else {
            // Replace < and > which avoids embeds on Discord
            token = args[1].replaceAll("(^<)|(>$)", "");

            // Try to use youtube timestamp if present
            Matcher m = ytTimestamp.matcher(args[1]);
            if (m.find()) {
                int ytDur = Integer.parseInt(m.group());
                dur = Duration.ofSeconds(ytDur);
            }

            if (args.length == 3) {
                MusicSeek seek = MusicUtils.getDurationFromArg(args[2]);
                if (seek == null) {
                    displayUsage(event.getChannel());
                    return;
                }
                dur = seek.dur();
            } else if (args.length > 3) {
                displayUsage(event.getChannel());
                return;
            }
        }

        var context = new TrackContext(
                ctx.guildID(),
                event.getChannel().getIdLong(),
                event.getAuthor().getIdLong(),
                dur.toMillis()
        );

        // Ask Lavaplayer for a track
        playerManager.loadTracks(token, ctx.musicManager(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                track.setUserData(context);
                ctx.musicManager().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> track.setUserData(context));
                if (isSearch) {
                    selectSearchResult(playlist, event, ctx.musicManager());
                } else {
                    ctx.musicManager().queuePlaylist(playlist);
                }
            }

            @Override
            public void noMatches() {
                event.getChannel().sendMessage("No matches").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void selectSearchResult(AudioPlaylist playlist, MessageReceivedEvent event, GuildMusicManager musicManager) {
        int resNum = Math.min(playlist.getTracks().size(), MAX_SEARCH_TRACKS);
        StringBuilder builder = new StringBuilder();
        List<Component> components = new ArrayList<>();
        List<Component> cancelRow = new ArrayList<>();

        builder.append(String.format("%d results. Please select the closest option or :x: to exit\n\n", resNum));

        for (int i = 0; i < resNum; i++) {
            AudioTrackInfo info = playlist.getTracks().get(i).getInfo();
            builder.append(String.format("%d) **%s** by `%s`\n",
                    i + 1,
                    info.title,
                    info.author
            ));

            Emoji emoji = Emoji.fromUnicode(String.format("U+003%d U+FE0F U+20E3", i + 1));
            components.add(Button.secondary("" + i, emoji));
        }

        cancelRow.add(Button.secondary("x", Emoji.fromUnicode("U+274C")));

        AtomicLong msgId = new AtomicLong();

        // Listen for buttons to select search buttons
        ListenerAdapter adapter = new ListenerAdapter() {
            @Override
            public void onButtonClick(@NotNull ButtonClickEvent event) {
                // Not the message we sent
                if (event.getMessage().getIdLong() != msgId.longValue()) {
                    return;
                }

                // Cancel search
                if (event.getComponentId().equals("x")) {
                    event.editMessage("Search cancelled")
                            .queue(msg -> msg.editOriginalComponents().queue());
                // User selected option
                } else {
                    int idx = Integer.parseInt(event.getComponentId());
                    AudioTrack nextTrack = playlist.getTracks().get(idx);
                    musicManager.queue(nextTrack);
                    event.editMessage(String.format("%s was selected", nextTrack.getInfo().title))
                            .flatMap(InteractionHook::editOriginalComponents)
                            .queue();
                }

                event.getJDA().removeEventListener(this);
            }
        };

        // Send search options
        event.getChannel()
                .sendMessage(builder.toString())
                .setActionRows(ActionRow.of(components), ActionRow.of(cancelRow))
                .queue(message -> {
                    // Remove buttons and search options after ~2 minutes
                    message.delete().queueAfter(120, TimeUnit.SECONDS,
                            msg -> event.getJDA().removeEventListener(adapter));

                    msgId.set(message.getIdLong());
                });

        event.getJDA().addEventListener(adapter);
    }

    private void displayUsage(MessageChannel channel) {
        channel.sendMessage("""
                Usage:
                $play <link> <Optional: seconds to skip OR HH:MM:SS>
                $play <search terms>""").queue();
    }
}
