package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.MusicSeek;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class SeekCommand extends ServerCommand {
    SeekCommand() {
        super("seek",
                CommandPermission.EVERYONE,
                """
                        Seek to location in current track. You can use + or - to seek relative to current place
                        
                        Usage:
                        %{p}seek Seconds
                        %{p}seek HH:MM:SS OR MM:SS>
                        %{p}seek +/-Seconds OR +/- HH:MM:SS OR +/- MM:SS
                        """);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var playerManager = WylxPlayerManager.getInstance();
        var guildID = event.getGuild().getIdLong();
        var musicManager = playerManager.getGuildManager(guildID);

        if (args.length != 2) {
            return;
        }

        if (musicManager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
            return;
        } else if (!MusicUtils.canUseVoiceCommand(guildID, event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        MusicSeek seekLoc = MusicUtils.getDurationFromArg(args[1]);

        if (seekLoc == null) {
            event.getChannel().sendMessage(getDescription(guildID)).queue();
            return;
        }

        String prettyTime = MusicUtils.getPrettyDuration(seekLoc.dur());
        musicManager.seek(seekLoc);
        String msg;
        if (seekLoc.relative()) {
            msg = String.format("Seeking %s", prettyTime);
        } else {
            msg = String.format("Now playing at %s", prettyTime);
        }

        event.getChannel().sendMessage(msg)
                .delay(Duration.ofMinutes(1))
                .flatMap(Message::delete)
                .queue();
    }
}
