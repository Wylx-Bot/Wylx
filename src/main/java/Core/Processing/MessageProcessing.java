package Core.Processing;

import Commands.DND.TTRPGPackage;
import Commands.Help;
import Commands.Management.ManagementPackage;
import Commands.Music.MusicPackage;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.Wylx;
import Core.ProcessPackage.ProcessPackage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MessageProcessing extends ListenerAdapter {
	public static final ProcessPackage[] processPackages = {new ManagementPackage(),
			new TTRPGPackage(),
			new MusicPackage()};
	public static final HashMap<String, ServerCommand> commandMap = new HashMap<>();
	public static final ArrayList<SilentEvent> events = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(MessageProcessing.class);

	static {
		commandMap.put("help", new Help());
		for(ProcessPackage processPackage : processPackages){
			for(ServerCommand command : processPackage.getCommands()){
				commandMap.put(command.getKeyword(), command);
			}
			events.addAll(Arrays.asList(processPackage.getEvents()));
		}
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		//Ignore messages from the bot
		if(event.getAuthor().getIdLong() == Wylx.getInstance().getBotID()) return;

		String prefix = "$"; // TODO: Get as server preference
		String msg = event.getMessage().getContentRaw();

		if(!event.isFromGuild()) return;
		// Check Commands if aimed at bot
		if (msg.startsWith(prefix)) {
			String[] args = msg.split(" ");
			String commandString = args[0].toLowerCase().replace(prefix, "");
			ServerCommand command = commandMap.get(commandString);

			if (command != null) {
				if(command.checkPermission(event)) {
					logger.debug("Command ({}) Called With {} Args", commandString, args.length);
					command.runCommand(event, args);
					return;
				} else {
					event.getMessage().reply("You don't have permission to use this command!").queue();
				}
			}
		}
		for(SilentEvent silentEvent : events){
			if(silentEvent.check(event)){
				silentEvent.runEvent(event);
				return;
			}
		}
	}
}
