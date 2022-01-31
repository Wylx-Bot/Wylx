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
                "Cleans the channel of bot interactions for the last X messages (20 by default), Requires Manage Messages" +
                "\nUsage: %{p}clean <Optional: number of messages to scrape>");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        long guildID = event.getGuild().getIdLong();
        String prefix = Wylx.getInstance().getPrefixThanksJosh(guildID);
        if(args.length == 1){
            cleanMessages(event.getChannel().getHistory(), 20, prefix);
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
            return;
        } else if(args.length != 2) {
            event.getMessage().reply(getDescription(prefix)).queue();
            return;
        }

        try{
            int scrape = Integer.parseInt(args[1]);
            if(scrape > 20) {
                Helper.validate("Are you sure you want to clean " + scrape + " messages", event,
                        () -> cleanMessages(event.getChannel().getHistory(), scrape, prefix));
            } else {
                event.getChannel().deleteMessageById(event.getMessageId()).queue();
                cleanMessages(event.getChannel().getHistory(), scrape, prefix);
            }
        } catch (NumberFormatException nfe){
            event.getMessage().reply(getDescription(prefix)).queue();
        }
    }

    public void cleanMessages(MessageHistory history, int scrape, String prefix){
        cleanMessages(history, scrape, prefix, new ArrayList<>());
    }

    public void cleanMessages(MessageHistory history, int scrape, String prefix, List<Message> toDelete){
        int scrapeThisTime = Math.min(scrape, 100);
        history.retrievePast(scrapeThisTime).queue(messages -> {
            // Load information that will be constant
            long botID = Wylx.getInstance().getBotID();

            // Loop through each loaded message
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);

                // If the author was the bot delete the message
                if(message.getAuthor().getIdLong() == botID){
                    toDelete.add(message);

                    // If the message before the bot message was calling the bot specifically also delete that message
                    if(i < scrapeThisTime-1 && messages.get(i+1).getContentRaw().length() > prefix.length()
                            && messages.get(i+1).getContentRaw().startsWith(prefix)){
                        toDelete.add(messages.get(i+1));
                    }
                }
            }

            if(scrape > 100){
                cleanMessages(history, scrape - 100, prefix, toDelete);
            } else {
                history.getChannel().purgeMessages(toDelete);
            }
        });
    }
}
