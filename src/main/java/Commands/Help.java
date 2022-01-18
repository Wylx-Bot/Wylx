package Commands;

import Core.Commands.ServerCommand;
import Core.ProcessPackage.ProcessPackage;
import Core.Processing.MessageProcessing;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class Help extends ServerCommand {
	public Help() {
		super("help", CommandPermission.EVERYONE);
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String[] args) {
		if(args.length == 1) {
			StringBuilder helpMessage = new StringBuilder();
			helpMessage.append("```diff\n");
			for (ProcessPackage processPackage : MessageProcessing.processPackages) {
				helpMessage.append(processPackage.getDescription());
			}
			helpMessage.append("```");
			event.getChannel().sendMessage(helpMessage).queue();
			return;
		}
	}
}
