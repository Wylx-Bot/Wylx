package com.wylx.wylxbot.Commands.Frog;

import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;


/**
 * Useful for commands where the bot pings a sender and receiver such as bonk and validate
 */
public abstract class SenderReceiverCommand extends ServerCommand {

    private final String[] noSpecifiedRecipientMsgs;
    private final String[] successfulMsgs;

    public SenderReceiverCommand(String keyword, CommandPermission cmdPerm, String description, String[] noSpecifiedRecipientMsgs, String[] successfulMsgs) {
        super(keyword, cmdPerm, description);
        this.noSpecifiedRecipientMsgs = noSpecifiedRecipientMsgs;
        this.successfulMsgs = successfulMsgs;
    }

    @Override
    public void runCommand(CommandContext ctx) {

        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();

        String senderMention = event.getAuthor().getAsMention();

        if(args.length == 1) {
            //no specified recipient
            event.getChannel().sendMessage(getRandomMsg(noSpecifiedRecipientMsgs).replace("@sender", senderMention)).queue();
        }
        else {
            event.getChannel().sendMessage(getRandomMsg(successfulMsgs).replace("@sender", senderMention).replace("@recipient", args[1])).queue();

        }
    }

    private String getRandomMsg(String[] msgs) {
        Random r = new Random();
        int index = r.nextInt(msgs.length);
        return msgs[index];
    }
}
