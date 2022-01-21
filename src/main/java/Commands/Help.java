package Commands;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;
import Core.Processing.MessageProcessing;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Locale;

public class Help extends ServerCommand {
	public Help() {
		super("help", CommandPermission.EVERYONE, "Provides lists and descriptions of commands");
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String[] args) {
		//If there are more than two args the user provided invalid input, correct them
		if(args.length > 2){
			event.getMessage().reply("Usage: $help <Keyword> or $help <CommandName>").queue();
			return;
		}

		//If there is only one arg print out the complete list of commands
		if(args.length == 1) {
			StringBuilder helpMessage = new StringBuilder();
			helpMessage.append("```diff\n");
			for (ProcessPackage processPackage : MessageProcessing.processPackages) {
				helpMessage.append(processPackage.getDescription());
			}
			helpMessage.append("```");
			event.getChannel().sendMessage(helpMessage).queue();
			return;
		} else {
			//Check command map for the arg to see if they provided the keyword for a command
			if(MessageProcessing.commandMap.containsKey(args[1].toLowerCase())){
				ServerCommand command = MessageProcessing.commandMap.get(args[1]);
				event.getChannel().sendMessage(command.getName() + ": " + command.getDescription()).queue();
				return;
			}

			//Check server commands to see if they provided the name of a server command
			for(ServerCommand command : MessageProcessing.commandMap.values()){
				if(command.getName().toLowerCase().equals(args[1].toLowerCase())){
					event.getChannel().sendMessage(command.getName() + ": " + command.getDescription()).queue();
					return;
				}
			}

			//Check SilentEvents to see if they provided the name of an event
			for(SilentEvent silentEvent : MessageProcessing.events){
				if(silentEvent.getName().toLowerCase().equals(args[1].toLowerCase())){
					event.getChannel().sendMessage(silentEvent.getName() + ": " + silentEvent.getDescription()).queue();
					return;
				}
			}
		}

		event.getChannel().sendMessage("Unable to find command: " + args[1]).queue();
	}
}
