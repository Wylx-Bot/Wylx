package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
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
        var context = new TrackContext(guildID, event.getChannel().getIdLong(), event.getAuthor().getIdLong());

        if (args.length != 2) {
            event.getChannel().sendMessage("Usage: $play <link>").queue();
            return;
        }

        playerManager.loadTracks(args[1], guildID, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                track.setUserData(context);
                event.getChannel().sendMessage("Track found")
                        .delay(Duration.ofSeconds(60))
                        .flatMap(Message::delete)
                        .queue();
                musicManager.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> track.setUserData(context));
                event.getChannel().sendMessage("Playlist found: Added " + playlist.getTracks().size() + " Songs")
                        .delay(Duration.ofSeconds(60))
                        .flatMap(Message::delete)
                        .queue();
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
