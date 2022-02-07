package Commands.Frog;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


/**
 * Useful for commands where the bot pings a sender and receiver such as $bonk and $validate
 */
public abstract class SenderReceiverCommand extends ServerCommand {

    private final String noSpecifiedRecipientMsg;
    private final String successfulMsg;

    public SenderReceiverCommand(String keyword, CommandPermission cmdPerm, String description, String noSpecifiedRecipientMsg, String successfulMsg) {
        super(keyword, cmdPerm, description);
        this.noSpecifiedRecipientMsg = noSpecifiedRecipientMsg;
        this.successfulMsg = successfulMsg;
    }

    @Override
    public void runCommand(CommandContext ctx) {

        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();

        String senderMention = event.getAuthor().getAsMention();

        if(args.length == 1) {
            //no specified recipient
            event.getChannel().sendMessage(noSpecifiedRecipientMsg.replace("@sender", senderMention)).queue();
        }
        else {
            event.getChannel().sendMessage(successfulMsg.replace("@sender", senderMention).replace("@recipient", args[1])).queue();

        }
    }
}
