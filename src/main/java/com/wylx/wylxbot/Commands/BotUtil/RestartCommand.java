package com.wylx.wylxbot.Commands.BotUtil;

import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RestartCommand extends ServerCommand {
    private static final int RESTART_EXIT_STATUS = 200;
    RestartCommand() {
        super("restart", CommandPermission.BOT_ADMIN, "Restart the bot...remotely!");
    }


    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        // TODO: Do cool stuff like...idk...
        // TODO: I don't actually know?

        logger.info("Restarting Wylx due to user command!");
        event.getChannel().sendMessage("Restarting Wylx...").queue();
        System.exit(RESTART_EXIT_STATUS);
    }
}
