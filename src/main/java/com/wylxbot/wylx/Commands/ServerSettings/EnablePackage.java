package com.wylxbot.wylx.Commands.ServerSettings;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.ServerEventManager;
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

		ServerEventManager eventManager = ServerEventManager.getServerEventManager(ctx.event().getGuild().getId());
		String packageName = ctx.args()[1].toLowerCase();
		boolean value = Boolean.parseBoolean(ctx.args()[2]);

		try{
			eventManager.setModule(packageName, value);
		} catch (IllegalArgumentException e){
			ctx.event().getChannel().sendMessage("Package \"" + packageName + "\" does not exist").queue();
			return;
		}

		ctx.event().getChannel().sendMessage("Set \"" + packageName + "\" to " + Boolean.parseBoolean(ctx.args()[2])).queue();
	}
}
