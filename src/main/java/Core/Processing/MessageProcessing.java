package Core.Processing;

import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;


public class MessageProcessing extends ListenerAdapter {
	private static final CommandPackage[] commandPackages = {};
	private static ArrayList<ServerCommand> commands = new ArrayList<>();

	static {
		for(CommandPackage commandPackage : commandPackages){
			commands.addAll(Arrays.asList(commandPackage.getCommands()));
		}
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		super.onMessageReceived(event);
	}
}
