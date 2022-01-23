package Commands.Frog;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BonkCommand extends SenderReceiverCommand {

//    public BonkCommand() {
//        super("bonk", CommandPermission.EVERYONE,
//                """
//                Bonks another user
//                Usage: $bonk <user tag>
//                """);
//    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
//        if(args.length == 1) {
//            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " has tried to bonk someone but missed!").queue();
//        }
//        else {
//            //if the user has a tag wth multiple words, account for that
//            StringBuilder tagBuilder = new StringBuilder();
//            for(int i=1; i<args.length; i++) {
//                tagBuilder.append(args[i]);
//                if(i != args.length-1) tagBuilder.append(" ");
//            }
//
//            String recipientTag;
//            try {
//                recipientTag = event.getGuild().getMemberByTag(tagBuilder.toString()).getAsMention();
//            } catch (IllegalArgumentException | NullPointerException e) {
//                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " has tried to bonk a user that doesn't exist here!").queue();
//                return;
//            }
//
//            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " has bonked " + recipientTag + "!").queue();
//        }
    }
}
