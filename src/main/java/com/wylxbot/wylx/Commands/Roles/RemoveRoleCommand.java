package com.wylxbot.wylx.Commands.Roles;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Database.DbElements.ServerIdentifiers;
import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RemoveRoleCommand extends ServerCommand {

    public RemoveRoleCommand() {
        super("unregisterrole", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, """
                        Remove role from list of roles which users can self-assign. Can be a comma-delimited list of either IDs or names.
                        Usage: %{p}unregisterrole role1, role2, role3, ...
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
            ctx.event().getChannel().sendMessage("Please specify roles to remove. You can give a list" +
                    "of comma-delimited role names or ids").queue();
            return;
        }

        List<String> rolesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg());
        List<Long> curRoles = ctx.db().getSetting(ServerIdentifiers.PublicRoles);
        StringBuilder removedRoles = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder();
        int oldSize = curRoles.size();

        List<Role> foundRoles = RoleUtil.getRolesFromStrings(rolesStr, guild, null);
        for (Role role : foundRoles) {
            long id = role.getIdLong();
            curRoles.remove(id);
            removedRoles.append(role.getAsMention()).append("\n");
        }

        removedRoles.append("Added ").append(oldSize - curRoles.size()).append(" roles!");

        embed.setAuthor("Removed roles");
        embed.setDescription(removedRoles.toString());
        ctx.event().getChannel().sendMessageEmbeds(embed.build()).queue();
        ctx.db().setSetting(ServerIdentifiers.PublicRoles, curRoles);
    }
}
