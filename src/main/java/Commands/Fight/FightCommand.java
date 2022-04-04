package Commands.Fight;

import Core.Commands.CommandContext;
import Core.Commands.ThreadedCommand;

public class FightCommand extends ThreadedCommand {

    public FightCommand() {
        super("fight", CommandPermission.EVERYONE, "Fight another user");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        // Check if

        // Check is fighting

        //
    }
}
