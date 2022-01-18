package Core.Processing;

import Commands.Management.ManagementPackage;
import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MessageProcessing extends ListenerAdapter {
	private static final CommandPackage[] commandPackages = {new ManagementPackage()};
	private static final HashMap<String, ServerCommand> commandMap = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(MessageProcessing.class);

	static {
		for(CommandPackage commandPackage : commandPackages){
			for(ServerCommand command : commandPackage.getCommands()){
				commandMap.put(command.getKeyword(), command);
			}
		}
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		String prefix = "$"; // TODO: Get as server preference
		String msg = event.getMessage().getContentRaw();

		// Reject if not aimed at us
		if (!msg.startsWith(prefix) ||
			!event.isFromGuild()) return;

		String[] args = msg.split(" ");
		String commandString = args[0].toLowerCase().replace(prefix, "");
		ServerCommand command = commandMap.get(commandString);

		if (command == null) return;
		if (!command.checkPermission(event)) {
			event.getMessage().reply("You don't have permission to use this command!").queue();
			return;
		}

		logger.debug("Command ({}) Called With {} Args", commandString, args.length);
		command.runCommand(event, args);
	}
}
