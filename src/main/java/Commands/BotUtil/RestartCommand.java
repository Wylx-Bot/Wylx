package Commands.BotUtil;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RestartCommand extends ServerCommand {
    private static final int RESTART_EXIT_STATUS = 200;
    RestartCommand() {
        super("restwrart", CommandPermission.BOT_ADMIN, "Restrawrt the bawt...remowtely! (❀˘꒳˘)♡(˘꒳˘❀)", "restart");
    }


    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        // TODO: Do cool stuff like...idk...
        // TODO: I don't actually know?

        logger.info("Restrawrting UWUylx duwe to uwser commawnd! ( ͡o ᵕ ͡o )");
        event.getChannel().sendMessage("Restrawrting UWUylx... ( ͡o ᵕ ͡o )").queue();
        System.exit(RESTART_EXIT_STATUS);
    }
}
