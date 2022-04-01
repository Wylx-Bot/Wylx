package Commands.BotUtil;

import Core.Commands.CommandContext;
import Core.Commands.ThreadedCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends ThreadedCommand {
    public PingCommand() {
        super("ping", CommandPermission.EVERYONE, "Pwovides the piwng fwom the bowt tuwu discowd *˚*(ꈍ ω ꈍ).₊̣̇.");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        JDA jda = event.getJDA();
        jda.getRestPing().queue(time -> {
            String msg = String.format("Powng! REST wesponse time was %d ms. UwUebsowcket piwng iws %s ms ♥(。U ω U。)\n",
                    time, jda.getGatewayPing());
            event.getChannel().sendMessage(msg).queue();
        });
    }
}
