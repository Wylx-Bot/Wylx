package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Roles.RoleMenu;
import Core.Roles.RoleReaction;
import Core.Wylx;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class AddRoleToMenuCommand extends ServerCommand {
    AddRoleToMenuCommand() {
        super("addroletomenu", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, "Add role to menu");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        String[] args = ctx.args();
        for (String arg : args) {
            System.out.println(arg);
        }

        RoleMenu menu = Wylx.getInstance().getDb().getRoleMenu(args[1]);
        if (menu == null) {
            ctx.event().getMessage().reply("Unknown role menu").queue();
            return;
        }

        List<Role> roles = ctx.event().getGuild().getRolesByName(args[2], false);
        Emoji emoji = Emoji.fromMarkdown(args[3]);
        menu.addReaction(new RoleReaction(roles.get(0), emoji));

        Wylx.getInstance().getDb().setRoleMenu(menu);
    }
}
