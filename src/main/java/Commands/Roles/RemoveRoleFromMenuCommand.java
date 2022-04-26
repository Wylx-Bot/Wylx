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

public class RemoveRoleFromMenuCommand extends ServerCommand {
    RemoveRoleFromMenuCommand() {
        super("removerolefrommenu", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, "Remove role from menu");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        String[] args = ctx.args();

        RoleMenu menu = Wylx.getInstance().getDb().getRoleMenu(args[1]);
        if (menu == null) {
            ctx.event().getMessage().reply("Unknown role menu").queue();
            return;
        }

        try {
            menu.removeReaction(args[2]);
            Wylx.getInstance().getDb().setRoleMenu(menu);
        } catch (Exception e) {
            ctx.event().getMessage().reply(e.getMessage()).queue();
        }
    }
}
