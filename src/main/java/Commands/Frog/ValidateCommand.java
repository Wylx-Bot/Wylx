package Commands.Frog;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ValidateCommand extends ServerCommand {

    public ValidateCommand() {
        super("validate", CommandPermission.EVERYONE,
                """
                        Remind another user that they matter to you
                        Usage: $validate <user tag>
                        """);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {

    }
}
