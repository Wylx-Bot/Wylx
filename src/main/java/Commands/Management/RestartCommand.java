package Commands.Management;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RestartCommand extends ServerCommand {
    private static final int EXIT_STATUS = 2046;
    RestartCommand() {
        super("restart", CommandPermission.BOT_ADMIN, "Restart the bot...remotely!");
    }


    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        // TODO: Do cool stuff like...idk...
        // TODO: I don't actually know?


        //AHAHHAH
        System.exit(EXIT_STATUS);
    }
}
