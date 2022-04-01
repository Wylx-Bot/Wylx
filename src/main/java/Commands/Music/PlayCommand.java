package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.*;
import Core.Util.Helper;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
                        Pway ow queue a song to pway
                        Uwusage:
                        %{p}pway <wink> <optionaw: seconds to skip or hh:mm:ss>
                        %{p}pway <seawch tewms>""",
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

        if (MusicUtils.voiceCommandBlocked(ctx)) {
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
                event.getChannel().sendMessage("No muwuatches").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage("Couwld nowt pway: " + exception.getMessage()).queue();
            }
        });
    }

    private void selectSearchResult(AudioPlaylist playlist, MessageReceivedEvent event, GuildMusicManager musicManager) {
        int resNum = Math.min(playlist.getTracks().size(), MAX_SEARCH_TRACKS);
        StringBuilder builder = new StringBuilder();
        List<ItemComponent> components = new ArrayList<>();
        List<ItemComponent> cancelRow = new ArrayList<>();

        builder.append(String.format("%d wesuwts. Pwease sewect da cwosest option ow :x: to exit\n\n", resNum));

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

        var toSend = event.getChannel().sendMessage(builder.toString());
        Helper.createButtonInteraction((ButtonInteractionEvent buttonEvent, Object obj) -> {
            // Cancel search
            if (buttonEvent.getComponentId().equals("x")) {
                buttonEvent.editMessage("Seawch cancewwed")
                        .flatMap(InteractionHook::editOriginalComponents)
                        .queue();
            } else {
                // User selected option
                int idx = Integer.parseInt(buttonEvent.getComponentId());
                AudioTrack nextTrack = playlist.getTracks().get(idx);
                musicManager.queue(nextTrack);
                buttonEvent.editMessage(String.format("%s uwas sewectwed", nextTrack.getInfo().title))
                        .flatMap(InteractionHook::editOriginalComponents)
                        .queue();
            }

            return true;
        }, (Message message, Boolean timedOut) -> {
            if (!timedOut) return;
            message.editMessage("Seawch timed owout")
                    .map(Message::editMessageComponents)
                    .queue();
        }, List.of(ActionRow.of(components),
                ActionRow.of(cancelRow)), toSend, null);
    }

    private void displayUsage(MessageChannel channel) {
        channel.sendMessage("""
                Uwusage:
                $pway <wink> <optionaw: seconds to skip or hh:mm:ss>
                $pway <seawch tewms>""").queue();
    }
}
