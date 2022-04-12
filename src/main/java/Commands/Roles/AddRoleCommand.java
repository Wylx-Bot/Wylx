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
        super("addrole", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,"""
                        Add role to list of roles which users can self-assign. Can be a comma-delimited list of either IDs or names.
                        Usage: {p}addrole role1, role2, role3, ...
                        """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        Guild guild = ctx.event().getGuild();

        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            ctx.event().getChannel().sendMessage("I do not have the `MANAGE_ROLES` permission!").queue();
            return;
        }

        if (ctx.args().length == 1) {
            ctx.event().getChannel().sendMessage("Please specify roles to add. You can give a list" +
                    "of comma-delimited role names or ids").queue();
            return;
        }

        List<String> rolesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg(), getKeyword());
        List<Long> curRoles = ctx.db().getSetting(ServerIdentifiers.PublicRoles);
        int oldSize = curRoles.size();

        List<Role> foundRoles = RoleUtil.getRolesFromStrings(rolesStr, guild, null);
        for (Role role : foundRoles) {
            long id = role.getIdLong();
            if (!curRoles.contains(id)) {
                curRoles.add(id);
            }
        }

        ctx.event().getChannel().sendMessage("Added *" + (curRoles.size() - oldSize) + "* roles!").queue();
        ctx.db().setSetting(ServerIdentifiers.PublicRoles, curRoles);
    }
}