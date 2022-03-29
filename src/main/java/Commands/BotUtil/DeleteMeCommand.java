package Commands.BotUtil;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Database.ServerIdentifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteMeCommand extends ServerCommand {
	public DeleteMeCommand() {
		super("test", CommandPermission.EVERYONE, "urmom");
	}

	@Override
	public void runCommand(CommandContext ctx) {
		CustomClass cust = ctx.db().getSetting(ServerIdentifiers.Modules);
		cust.setOne(cust.getOne() + 1);
		cust.setTwo(cust.getTwo() + cust.getTwo().length());
		ctx.event().getChannel().sendMessage("one: " + cust.getOne() + " two: " + cust.getTwo()).queue();
		ctx.db().setSetting(ServerIdentifiers.Modules, cust);
	}
}
