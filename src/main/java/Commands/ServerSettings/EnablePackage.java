package Commands.ServerSettings;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Events.ServerEventManager;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.Permission;

public class EnablePackage extends ServerCommand {
	public EnablePackage() {
		super("enablepackage", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, """
				Enable and disable specific command packages
				Usage: %{p}enablepackage <package_name> <true or false>
				""", "package");
	}

	@Override
	public void runCommand(CommandContext ctx) {
		if(ctx.args().length != 3){
			ctx.event().getChannel().sendMessage(getDescription(ctx.prefix())).queue();
			return;
		}

		ServerEventManager eventManager = ServerEventManager.getServerEventManager(ctx.guildID());
		String packageName = ctx.args()[1].toLowerCase();
		boolean value = Boolean.parseBoolean(ctx.args()[2]);

		try{
			eventManager.setModule(packageName, value);
		} catch (IllegalArgumentException e){
			ctx.event().getChannel().sendMessage("Package \"" + packageName + "\" does not exist").queue();
			return;
		}

		ctx.db().setSetting(ctx.guildID(), ServerIdentifiers.Modules, eventManager.getDocument());
		ctx.event().getChannel().sendMessage("Set \"" + packageName + "\" to " + Boolean.parseBoolean(ctx.args()[2])).queue();
	}
}
