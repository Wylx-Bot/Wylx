package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand extends ServerCommand {
    public SkipCommand() {
        super("skip", CommandPermission.EVERYONE);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());
        manager.skip();
    }
}
