package Commands.ServerUtil;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Util.Helper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends ServerCommand {
    public ClearCommand() {
        super("clear", CommandPermission.DISCORD_PERM, Permission.MESSAGE_MANAGE,
                "Delete the most recent X messages from the channel, Requires Manage Messages Perm" +
                "\nUsage: %{p}clear <messages to delete>");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();
        if(args.length != 2){
            event.getMessage().reply(getDescription(ctx.prefix())).queue();
            return;
        }

        try {
            int toDelete = Integer.parseInt(args[1]);
            if(toDelete > 10) {
                Helper.validate("Are you sure you want to delete " + toDelete + " messages", event,
                        () -> clearMessages(event.getChannel().getHistory(), toDelete + 2));
            } else {
                clearMessages(event.getChannel().getHistory(), toDelete + 1);
            }
        } catch(NumberFormatException nfe){
            event.getMessage().reply(getDescription(ctx.prefix())).queue();
        }
    }

    public void clearMessages(MessageHistory history, int count){
        clearMessages(history, count, new ArrayList<>());
    }

    public void clearMessages(MessageHistory history, int count, List<Message> messageDeletes){
        int toDelete = Math.min(count, 100);
        history.retrievePast(toDelete).queue(messages -> {
            messageDeletes.addAll(messages);
            if(count > 100){
                clearMessages(history, count - 100, messageDeletes);
            } else {
                history.getChannel().purgeMessages(messageDeletes);
            }
        });
    }
}