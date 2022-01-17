package Core.Processing;

import Commands.Management.ManagementPackage;
import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class MessageProcessing extends ListenerAdapter {
	private static final CommandPackage[] commandPackages = {new ManagementPackage()};
	private static HashMap<String, ServerCommand> commandMap = new HashMap<>();

	static {
		for(CommandPackage commandPackage : commandPackages){
			for(ServerCommand command : commandPackage.getCommands()){
				commandMap.put(command.getKeyword(), command);
			}
		}
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		String prefix = "$";
		ServerCommand command = commandMap.get(event.getMessage().getContentRaw().toLowerCase().split(" ")[0].replace(prefix, ""));
		System.out.println(command);
		if(command != null){
			command.runCommand(event);
		}
	}
}
