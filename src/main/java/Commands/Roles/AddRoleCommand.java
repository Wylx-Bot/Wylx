package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Util.RoleUtil;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class AddRoleCommand extends ServerCommand {

    public AddRoleCommand() {
        super("registerrole", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,"""
                        Add role to list of roles which users can self-assign. Can be a comma-delimited list of either IDs or names.
                        Usage: %{p}registerrole role1, role2, role3, ...
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

        List<String> rolesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg());
        List<Long> curRoles = ctx.db().getSetting(ServerIdentifiers.PublicRoles);
        StringBuilder addedRoles = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder();
        int oldSize = curRoles.size();

        List<Role> foundRoles = RoleUtil.getRolesFromStrings(rolesStr, guild, null);
        for (Role role : foundRoles) {
            long id = role.getIdLong();
            if (!curRoles.contains(id)) {
                curRoles.add(id);
                addedRoles.append(role.getAsMention()).append("\n");
            }
        }

        addedRoles.append("Added ").append(curRoles.size() - oldSize).append(" roles!");

        embed.setAuthor("Added roles");
        embed.setDescription(addedRoles.toString());
        ctx.event().getChannel().sendMessageEmbeds(embed.build()).queue();
        ctx.db().setSetting(ServerIdentifiers.PublicRoles, curRoles);
    }
}
