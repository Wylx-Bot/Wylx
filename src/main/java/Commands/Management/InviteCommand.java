package Commands.Management;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InviteCommand extends ServerCommand {
	private final static String URL = "https://discord.com/api/oauth2/authorize?client_id=933557997328793691&permissions=277293853696&scope=bot";

	public InviteCommand() {
		super("invite", CommandPermission.EVERYONE, "Get a link to add the bot to your server");
	}

	@Override
	public void runCommand(MessageReceivedEvent event, CommandContext ctx) {
		event.getChannel().sendMessage("Click here to invite me to your server: " + URL).queue();
	}
}
