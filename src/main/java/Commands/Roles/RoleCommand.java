package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Role.RoleUtil;
import Database.DiscordServer;
import Database.ServerIdentifiers;

import java.util.Arrays;
import java.util.List;

public class RoleCommand extends ServerCommand {

    RoleCommand() {
        super("role", CommandPermission.EVERYONE, "List and assign roles");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        DiscordServer db = ctx.db();

        List<String> rolesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg(), getKeyword());
        List<Long> list = db.getSetting(ServerIdentifiers.PublicRoles);



    }
}
