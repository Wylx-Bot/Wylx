package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends ServerCommand {
    PauseCommand() {
        super("pause",
                CommandPermission.EVERYONE,
                "Pause current song");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());
        long guildID = event.getGuild().getIdLong();
        long memberID = event.getAuthor().getIdLong();
        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(guildID, memberID)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            manager.pause(true);
        }
    }
}
