package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import Core.Music.TrackContext;
import Core.Music.WylxPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class PlayCommand extends ServerCommand {
    public PlayCommand() {
        super("play", CommandPermission.EVERYONE);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var playerManager = WylxPlayerManager.getInstance();
        var guildID = event.getGuild().getIdLong();
        var musicManager = playerManager.getGuildManager(event.getGuild().getIdLong());

        if (args.length < 2 || args.length > 3) {
            event.getChannel().sendMessage("Usage: $play link <Seconds to skip OR HH:MM:SS>").queue();
            return;
        }

        if (!MusicUtils.canUseVoiceCommand(guildID, event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        // Replace < and > which avoids embeds on Discord
        args[1] = args[1].replaceAll("(^<)|(>$)", "");

        // Get start location if user gives time
        Duration dur = Duration.ofSeconds(0);
        if (args.length == 3) {
            dur = MusicUtils.getDurationFromArg(args[2]);
        }

        var context = new TrackContext(
                guildID,
                event.getChannel().getIdLong(),
                event.getAuthor().getIdLong(),
                dur.toMillis()
        );

        // Ask Lavaplayer for a track
        playerManager.loadTracks(args[1], guildID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                track.setUserData(context);
                musicManager.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> track.setUserData(context));
                musicManager.queuePlaylist(playlist);
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
}
