package com.wylxbot.wylx.Commands.ServerUtil;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Util.Helper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClearToCommand extends ServerCommand {
    public ClearToCommand() {
        super("clearto", CommandPermission.DISCORD_PERM, Permission.MESSAGE_MANAGE,
                "Clears to the provided message id or the replied to message (inclusive)");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();
        if(args.length == 2) {
            try{
                long messageID = Long.parseLong(args[1]);
                deleteToID(event, messageID);
            } catch (NumberFormatException nfe){
                event.getMessage().reply(getDescription(ctx.prefix())).queue();
            }
        } else if (args.length == 1){
            try {
                long messageID = event.getMessage().getMessageReference().getMessageIdLong();
                deleteToID(event, messageID);
            } catch (NullPointerException npe){
                event.getMessage().reply(getDescription(ctx.prefix())).queue();
            }
        }
    }

    private void deleteToID(MessageReceivedEvent event, long messageID) {
        event.getChannel().retrieveMessageById(messageID).queue((msg) -> {
            MessageHistory history = event.getChannel().getHistory();

            retrieveTo(messageID, history, (retrieved) -> {
                if(retrieved.size() > 10){
                    Helper.validate("Are you sure you want to delete " + retrieved.size() + " messages",
                            event,
                            () -> event.getChannel().purgeMessages(retrieved));
                } else {
                    event.getChannel().purgeMessages(retrieved);
                }
            });
        }, (failure) -> {
            event.getMessage().reply("That message doesn't exist here!").queue();
        });
    }

    private void retrieveTo(long id, MessageHistory history, Consumer<List<Message>> consumer){
        retrieveTo(id, history, new ArrayList<>(), consumer);
    }

    private void retrieveTo(long id, MessageHistory history, List<Message> messageList, Consumer<List<Message>> consumer){
        history.retrievePast(100).queue(messages -> {
            for(Message msg : messages){
                messageList.add(msg);
                if(msg.getIdLong() == id){
                    consumer.accept(messageList);
                    return;
                }
            }
            retrieveTo(id, history, messageList, consumer);
        });
    }
}
