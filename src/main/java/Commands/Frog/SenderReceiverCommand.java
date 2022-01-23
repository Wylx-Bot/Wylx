package Commands.Frog;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


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
            event.getChannel().sendMessage(noSpecifiedRecipientMsg.replace("*sender", senderMention)).queue();
        }
        else {
            //if the user has a tag wth multiple words, account for that
            StringBuilder tagBuilder = new StringBuilder();
            for(int i=1; i<args.length; i++) {
                tagBuilder.append(args[i]);
                if(i != args.length-1) tagBuilder.append(" ");
            }

            String recipientMention;
            try {
                recipientMention = event.getGuild().getMemberByTag(tagBuilder.toString()).getAsMention();
            } catch (IllegalArgumentException | NullPointerException e) {
                event.getChannel().sendMessage(recipientNotFoundMsg).queue();
                return;
            }

            event.getChannel().sendMessage(successfulMsg.replace("*sender", senderMention).replace("*recipient", recipientMention)).queue();
        }
    }
}
