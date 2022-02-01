package Commands.Management;

import Core.Commands.CommandContext;
import Core.Commands.ThreadedCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends ThreadedCommand {
    public PingCommand() {
        super("ping", CommandPermission.EVERYONE, "Provides the ping from the bot to discord");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        JDA jda = event.getJDA();
        jda.getRestPing().queue(time -> {
            String msg = String.format("Pong! REST response time was %d ms. Websocket ping is %s ms\n",
                    time, jda.getGatewayPing());
            event.getChannel().sendMessage(msg).queue();
        });
    }
}
