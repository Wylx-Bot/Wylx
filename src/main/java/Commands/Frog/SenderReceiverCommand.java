package Commands.Frog;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;


/**
 * Useful for commands where the bot pings a sender and receiver such as $bonk and $validate
 */
public abstract class SenderReceiverCommand extends ServerCommand {

    private String noSpecifiedRecipientMsg;
    private String recipientNotFoundMsg;
    private String successfulMsg;

    public SenderReceiverCommand(String keyword, CommandPermission cmdPerm, String description, String noSpecifiedRecipientMsg, String recipientNotFoundMsg, String successfulMsg) {
        super(keyword, cmdPerm, description);
        this.noSpecifiedRecipientMsg = noSpecifiedRecipientMsg;
        this.recipientNotFoundMsg = recipientNotFoundMsg;
        this.successfulMsg = successfulMsg;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {

        String senderMention = event.getAuthor().getAsMention();

        if(args.length == 1) {
            event.getChannel().sendMessage(noSpecifiedRecipientMsg.replace("@sender", senderMention)).queue();
        }
        else {

            for(Member member : event.getGuild().getMembers()) {
                if(member.getAsMention().equals(args[1])) {
                    event.getChannel().sendMessage(successfulMsg.replace("@sender", senderMention).replace("@recipient", args[1])).queue();
                    return;
                }
            }

            event.getChannel().sendMessage(recipientNotFoundMsg.replace("@sender", senderMention)).queue();

//            String receiverMention;
//            try {
//                event.getGuild().getMemberByTag(tagBuilder.toString());
//            } catch (IllegalArgumentException | NullPointerException e) {
//                event.getChannel().sendMessage(recipientNotFoundMsg.replace("@sender", senderMention)).queue();
//                return;
//            }


        }
    }
}
