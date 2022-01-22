package Commands.Management;

import Core.Commands.ServerCommand;
import Core.Wylx;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CleanCommand extends ServerCommand {
    public CleanCommand() {
        super("clean", CommandPermission.DISCORD_PERM, Permission.MESSAGE_MANAGE,
                "Deletes messages the bot has sent and calls to the bot from the last 100 messages in this channel");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        event.getChannel().deleteMessageById(event.getMessage().getIdLong()).queue();
        event.getChannel().getHistory().retrievePast(100).queue(messages -> {
            // Load information that will be constant
            long botID = Wylx.getInstance().getBotID();
            char prefix = '$';

            // Loop through each loaded message
            for(int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);

                // If the author was the bot delete the message
                if(message.getAuthor().getIdLong() == botID){
                    message.getChannel().deleteMessageById(message.getIdLong()).queue();

                    // If the message before the bot message was calling the bot specifically also delete that message
                    if(i < 100 && messages.get(i+1).getContentRaw().charAt(0) == prefix){
                        message.getChannel().deleteMessageById(messages.get(i+1).getIdLong()).queue();
                    }

                    // Put thread to sleep after making api requests so that we dont get rate limited
                    try {
                        Thread.sleep(2200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
