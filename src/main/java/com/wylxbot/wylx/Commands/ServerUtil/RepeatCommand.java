package com.wylxbot.wylx.Commands.ServerUtil;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ThreadedCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RepeatCommand extends ThreadedCommand {
    public RepeatCommand() {
        super("repeat", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "%{p}repeat <int x> <str msg> Repeats message msg x times, only accessible to admins");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();
        int x;
        if(args.length < 3){
            event.getMessage().reply("Usage $repeat <int x> <str msg>").queue();
            return;
        }
        try{
            x = Integer.parseInt(args[1]);
        } catch(NumberFormatException nfe) {
            event.getMessage().reply("Unable to turn " + args[1] + " to a number\nUsage $repeat <int x> <str msg>").queue();
            return;
        }

        String msg = "";
        for(int i = 2; i < args.length; i++){
            msg += args[i] + " ";
        }
        for(int i = x; i > 0; i--){
            event.getChannel().sendMessage(msg).queue();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
