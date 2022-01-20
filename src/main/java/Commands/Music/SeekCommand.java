package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class SeekCommand extends ServerCommand {
    SeekCommand() {
        super("seek", CommandPermission.EVERYONE);
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
        } else if (!MusicUtils.canUseVoiceCommand(guildID, event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            Duration seekLoc = MusicUtils.getDurationFromArg(args[1]);
            String prettyTime = MusicUtils.getPrettyDuration(seekLoc);
            musicManager.seek(seekLoc);
            event.getChannel().sendMessage(String.format("Skipped to %s", prettyTime))
                    .delay(Duration.ofMinutes(1))
                    .flatMap(Message::delete)
                    .queue();
        }
    }
}
