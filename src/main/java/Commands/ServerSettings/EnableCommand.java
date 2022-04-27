package Commands.ServerSettings;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Events.ServerEventManager;
import Core.Processing.MessageProcessing;
import Database.ServerIdentifiers;
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

		ServerEventManager eventManager = ServerEventManager.getServerEventManager(ctx.guildID());
		String eventName;

		try{
			eventName = MessageProcessing.eventMap.get(ctx.args()[1].toLowerCase()).getClass().getSimpleName().toLowerCase(Locale.ROOT);;
			boolean value = Boolean.parseBoolean(ctx.args()[2]);
			eventManager.setEvent(eventName, value);
		} catch (IllegalArgumentException e){
			ctx.event().getChannel().sendMessage(e.getMessage()).queue();
			return;
		}

		ctx.db().setSetting(ctx.guildID(), ServerIdentifiers.Modules, eventManager.getDocument());
		ctx.event().getChannel().sendMessage("Set \"" + eventName + "\" to " + Boolean.parseBoolean(ctx.args()[2])).queue();
	}
}
