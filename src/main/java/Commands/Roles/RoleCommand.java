package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Util.RoleUtil;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoleCommand extends ServerCommand {

    RoleCommand() {
        super("role", CommandPermission.EVERYONE, """
                List and assign roles. When entering role names, either use the role ID or role name.
                Usage:
                To list roles: %{p}role
                To give/remove roles: %{p}role role1, role2, role3
                """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        Guild guild = ctx.event().getGuild();
        DiscordServer db = ctx.db();

        List<Long> roleIdsDb = db.getSetting(ServerIdentifiers.PublicRoles);
        List<Role> rolesDb = RoleUtil.getRolesFromIds(roleIdsDb, guild);

        // If no args provided, just list which roles users can give themselves
        if (ctx.args().length == 1) {
            roleListEmbed(ctx, rolesDb);
            return;
        }

        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            ctx.event().getChannel().sendMessage("I do not have the `MANAGE_ROLES` permission!").queue();
            return;
        }

        modifyRolesOnUser(ctx, rolesDb);
    }

    private void roleListEmbed(CommandContext ctx, List<Role> rolesDb) {
        Guild guild = ctx.event().getGuild();
        if (rolesDb.size() == 0) {
            ctx.event().getChannel().sendMessage("No roles are assigned yet to be given out! " +
                    "Use the AddRole command to add assignable roles").queue();
            return;
        }

        rolesDb.sort(Comparator.comparing(Role::getName));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(String.format("%s roles", guild.getName()), guild.getIconUrl());

        StringBuilder desc = new StringBuilder("Here are the current roles that are assignable: ");

        for (Role role : rolesDb) {
            desc.append(String.format("\n%s", role.getAsMention()));
        }

        desc.append("\n\n");
        desc.append("You can assign these roles by using ").append(ctx.prefix()).append("role <names of roles>\n");
        desc.append("EX: `").append(ctx.prefix()).append("role ").append(rolesDb.get(0).getName());
        if (rolesDb.size() != 1) {
            desc.append(", ");
            desc.append(rolesDb.get(1).getName());
        }
        desc.append("`");

        embed.setDescription(desc.toString());
        ctx.event().getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void modifyRolesOnUser(CommandContext ctx, List<Role> rolesDb) {
        Guild guild = ctx.event().getGuild();
        Member user = ctx.event().getMember();

        assert user != null;
        List<Role> userRoles = user.getRoles();
        List<String> invalidRoles = new ArrayList<>();

        // More than one arg, user is trying to add or remove roles
        List<String> roleNamesStr = RoleUtil.commaArrayStripKeyword(ctx.parsedMsg(), getKeyword());
        List<Role> rolesStr = RoleUtil.getRolesFromStrings(roleNamesStr, guild, invalidRoles);

        StringBuilder invStr = new StringBuilder(); // Invalid roles which don't exist
        StringBuilder ripStr = new StringBuilder(); // Roles which exist but not in Wylx DB
        StringBuilder addStr = new StringBuilder(); // Roles added from user
        StringBuilder delStr = new StringBuilder(); // Roles removed from user

        for (Role role : rolesStr) {
            if (!rolesDb.contains(role)) {
                ripStr.append(role.getAsMention()).append(" ");
                continue;
            }

            if (userRoles.contains(role)) {
                guild.removeRoleFromMember(user, role).queue();
                delStr.append(role.getAsMention()).append(" ");
            } else {
                guild.addRoleToMember(user, role).queue();
                addStr.append(role.getAsMention()).append(" ");
            }
        }

        for (String invalid : invalidRoles) {
            invStr.append("`").append(invalid).append("` ");
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(String.format("%s roles", guild.getName()), guild.getIconUrl());
        if (!addStr.isEmpty()) embed.addField("Roles added", addStr.toString(), false);
        if (!delStr.isEmpty()) embed.addField("Roles removed", delStr.toString(), false);
        if (!ripStr.isEmpty()) embed.addField("Non-public roles", ripStr.toString(), false);
        if (!invStr.isEmpty()) embed.addField("Invalid roles", invStr.toString(), false);
        ctx.event().getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
