package Commands.ServerUtil;

import Core.Commands.CommandContext;
import Core.Commands.ThreadedCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RepeatCommand extends ThreadedCommand {
    public RepeatCommand() {
        super("repeat", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "%{p}rawrepeat <int x> <stw msg> wepeats message msg x times, onwy accessibwe tuwu admins");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();
        int x;
        if(args.length < 3){
            event.getMessage().reply("UwUsage $rawrpeat <int x> <stw msg>").queue();
            return;
        }
        try{
            x = Integer.parseInt(args[1]);
        } catch(NumberFormatException nfe) {
            event.getMessage().reply("Unabwe tuwu tuwn \" + awgs[1] + \" tuwu a numbew\\nusage $wepeat <int x> <stw msg> (。U⁄ ⁄ω⁄ ⁄ U。)").queue();
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
