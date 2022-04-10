package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Role.RoleUtil;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class AddRoleCommand extends ServerCommand {

    public AddRoleCommand() {
        super("addrole", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "Add role to list of roles which users can self-assign. Can be a comma-delimited list" +
                        " (addrole role1, role2, role3, ...)");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        Guild guild = ctx.event().getGuild();

        if (ctx.args().length == 1) {
            ctx.event().getChannel().sendMessage("Please specify a role to add").queue();
            return;
        }

        List<String> rolesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg(), getKeyword());
        List<Long> curRoles = ctx.db().getSetting(ServerIdentifiers.PublicRoles);
        int oldSize = curRoles.size();

        // Check that all roles exist and then add to assignable roles
        for (String roleStr : rolesStr) {
            // Try seeing if it's an ID first
            try {
                Role roleObj = guild.getRoleById(roleStr);
                if (roleObj != null && !curRoles.contains(roleObj.getIdLong())) {
                    curRoles.add(roleObj.getIdLong());
                    continue;
                }
            } catch (NumberFormatException e) {
                // NOOP, may be a name
            }

            // Try search for the role by name
            List<Role> roleObjs = guild.getRolesByName(roleStr, true);
            for (Role roleObj : roleObjs) {
                if (!curRoles.contains(roleObj.getIdLong())) {
                    curRoles.add(roleObj.getIdLong());
                }
            }
        }

        ctx.event().getChannel().sendMessage("Added *" + (curRoles.size() - oldSize) + "* roles!").queue();
        ctx.db().setSetting(ServerIdentifiers.PublicRoles, curRoles);
    }
}
