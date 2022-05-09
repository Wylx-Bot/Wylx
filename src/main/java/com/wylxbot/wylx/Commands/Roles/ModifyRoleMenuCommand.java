package com.wylxbot.wylx.Commands.Roles;

import com.wylxbot.wylx.Commands.Roles.RolesUtil.DMListener;
import com.wylxbot.wylx.Commands.Roles.RolesUtil.DMListenerUserManager;
import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleMenu;
import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleReaction;
import com.wylxbot.wylx.Wylx;
import com.wylxbot.wylx.Database.DbElements.DiscordRoleMenu;
import com.wylxbot.wylx.Database.DbElements.RoleMenuIdentifiers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ModifyRoleMenuCommand extends ServerCommand {
    ModifyRoleMenuCommand() {
        super("modifyrolemenu", CommandPermission.DISCORD_PERM, Permission.MANAGE_ROLES,
                """
                        Modify role menu by setting the title, removing roles, and adding roles to a menu.
                        Message ID is shown at the bottom of a role menu.
                        Usage: %{p}modifyrolemenu <message id>
                        """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        User user = ctx.event().getMessage().getAuthor();

        if (ctx.args().length != 2) {
            ctx.event().getMessage().reply("Please provide a message id").queue();
            return;
        }

        DiscordRoleMenu roleDb = Wylx.getInstance().getDb().getRoleMenu(ctx.args()[1]);
        RoleMenu menu = roleDb.getSettingOrNull(RoleMenuIdentifiers.ROLE_MENU);
        if (menu == null) {
            ctx.event().getMessage().reply("Could not find menu").queue();
            return;
        }

        if (!menu.getGuildID().equals(ctx.guildID())) {
            String str = String.format("Menu is from another server, please run %smodifyRoleMenu from that server.", ctx.prefix());
            ctx.event().getMessage().reply(str).queue();
            return;
        }

        // DMs may be blocked, attempt to send DM before adding a listener
        try {
            DMListener listener = new DMListener(menu, user, ctx.event().getGuild());
            DMListenerUserManager.getInstance().addDMListener(user, listener);
        } catch (ErrorResponseException e) {
            ctx.event().getMessage().reply("User private messages are blocked! Make sure that DMs are enabled for this guild.").queue();
        }
    }
}
