package Commands.Management;

import Core.Commands.ThreadedCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends ThreadedCommand {
    public PingCommand() {
        super("ping", CommandPermission.EVERYONE);
    }

    @Override
    protected void runCommandThread(MessageReceivedEvent event, String[] args) {
        JDA jda = event.getJDA();
        jda.getRestPing().queue(time -> {
            String msg = String.format("Pong! REST response time was %d ms. Websocket ping is %s ms\n",
                    time, jda.getGatewayPing());
            event.getChannel().sendMessage(msg).queue();
        });
    }
}
