package Commands.Management;

import Core.Commands.ServerCommand;
import Core.Util.Helper;
import Core.Wylx;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CleanCommand extends ServerCommand {
    public CleanCommand() {
        super("clean", CommandPermission.DISCORD_PERM, Permission.MESSAGE_MANAGE,
                "Cleans the channel of bot interactions for the last X messages (20 by default)" +
                "\nUsage: $clean <Optional: number of messages to scrape>");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        if(args.length == 1){
            cleanMessages(event.getChannel().getHistory(), 20);
        } else if(args.length == 2){
            try{
                int scrape = Integer.parseInt(args[1]);
                if(scrape > 20) {
                    Helper.validate("Are you sure you want to clean " + scrape + " messages", event, () -> cleanMessages(event.getChannel().getHistory(), scrape));
                } else {
                    cleanMessages(event.getChannel().getHistory(), scrape);
                }
            } catch (NumberFormatException nfe){
                event.getMessage().reply(getDescription()).queue();
            }
        } else {
            event.getMessage().reply(getDescription()).queue();
        }
    }

    public void cleanMessages(MessageHistory history, int scrape){
        cleanMessages(history, scrape, new ArrayList<>());
    }

    public void cleanMessages(MessageHistory history, int scrape, List<Message> toDelete){
        int scrapeThisTime = Math.min(scrape, 100);
        history.retrievePast(scrapeThisTime).queue(messages -> {
            // Load information that will be constant
            long botID = Wylx.getInstance().getBotID();
            char prefix = '$';

            // Loop through each loaded message
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);

                // If the author was the bot delete the message
                if(message.getAuthor().getIdLong() == botID){
                    toDelete.add(message);

                    // If the message before the bot message was calling the bot specifically also delete that message
                    if(i < scrapeThisTime-1 && messages.get(i+1).getContentRaw().length() > 1  && messages.get(i+1).getContentRaw().charAt(0) == prefix){
                        toDelete.add(messages.get(i+1));
                    }
                }
            }
            if(scrape > 100){
                cleanMessages(history, scrape - 100, toDelete);
            } else {
                history.getChannel().purgeMessages(toDelete);
            }
        });
    }
}
