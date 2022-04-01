package Commands;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;
import Core.Processing.MessageProcessing;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;

public class Help extends ServerCommand {
    public Help() {
        super("helwp", CommandPermission.EVERYONE, "Pwovides wists awnd descwiptions of commands ( ᴜ ω ᴜ )", "help");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();

        //If there are more than two args the user provided invalid input, correct them
        if(args.length > 2){
            event.getMessage().reply("Usage: %{p}helwp <Keywoord>".replaceAll("%\\{p}",
                    Matcher.quoteReplacement(ctx.prefix()))).queue();
            return;
        }

        //If there is only one arg print out the complete list of commands
        if(args.length == 1) {
            StringBuilder helwpMessawge = new StringBuilder();
            helwpMessawge.append("```diff\n");
            for (ProcessPackage processPackage : MessageProcessing.processPackages) {
                helwpMessawge.append(processPackage.getDescription());
            }
            helwpMessawge.append("```");
            event.getChannel().sendMessage(helwpMessawge).queue();
            return;
        } else {
            //Check command map for the arg to see if they provided the keyword for a command
            if(MessageProcessing.commandMap.containsKey(args[1].toLowerCase())){
                ServerCommand command = MessageProcessing.commandMap.get(args[1]);
                event.getChannel().sendMessage(command.getName() + ": " + command.getDescription(ctx.prefix())).queue();
                return;
            }

            //Check SilentEvents to see if they provided the name of an event
            for(SilentEvent silentEvent : MessageProcessing.events){
                if(silentEvent.getName().equalsIgnoreCase(args[1])){
                    event.getChannel().sendMessage(silentEvent.getName() + ": " + silentEvent.getDescription()).queue();
                    return;
                }
            }
        }

        event.getChannel().sendMessage("Unawble tuwu find commawnd: " + args[1]).queue();
    }
}
