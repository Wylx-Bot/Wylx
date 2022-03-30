package Commands.ServerSettings;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Events.ServerEventManager;
import Core.Processing.MessageProcessing;
import net.dv8tion.jda.api.Permission;

import java.util.Locale;

public class EnableCommand extends ServerCommand {
	public EnableCommand() {
		super("enablecommand", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, """
				Enable and disable specific commands
				Usage: %{p}enablecommand <command_name> <true or false>
				""", "command");
	}

	@Override
	public void runCommand(CommandContext ctx) {
		if(ctx.args().length != 3){
			ctx.event().getChannel().sendMessage(getDescription(ctx.prefix())).queue();
			return;
		}

		ServerEventManager eventManager = ServerEventManager.getServerEventManager(ctx.event().getGuild().getId());
		String eventName = ctx.args()[1];

		try{
			eventName = MessageProcessing.eventMap.get(ctx.args()[1].toLowerCase()).getClass().getSimpleName().toLowerCase(Locale.ROOT);;
			boolean value = Boolean.parseBoolean(ctx.args()[2]);
			eventManager.setEvent(eventName, value);
		} catch (Exception e){
			ctx.event().getChannel().sendMessage("Event \"" + eventName + "\" does not exist").queue();
			return;
		}

		ctx.event().getChannel().sendMessage("Set \"" + eventName + "\" to " + Boolean.parseBoolean(ctx.args()[2])).queue();
	}
}
