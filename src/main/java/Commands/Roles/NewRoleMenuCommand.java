package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Commands.Roles.RolesUtil.RoleMenu;
import Core.Wylx;
import Database.RoleMenuIdentifiers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class NewRoleMenuCommand extends ServerCommand {
    public NewRoleMenuCommand() {
        super("newrolemenu", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
            """
            Create a new Role menu
            Usage:
            %{p}newrolemenu #channel-mention
            """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        List<TextChannel> channels = event.getMessage().getMentionedChannels();
        if (channels.size() != 1) {
            event.getMessage().reply("Please mention 1 channel").queue();
            return;
        }

        TextChannel newChannel = channels.get(0);
        if (!newChannel.canTalk()) {
            event.getMessage().reply("Wylx does not have permission to send messages in that channel!").queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        if (!self.hasPermission(newChannel, Permission.MESSAGE_ADD_REACTION)) {
            event.getMessage().reply("Wylx does not have permission to react in that channel!").queue();
            return;
        }

        if (!self.hasPermission(newChannel, Permission.MESSAGE_MANAGE)) {
            event.getMessage().reply("Wylx does not have the permission MESSAGE_MANAGE in that channel!").queue();
            return;
        }

        Message newMessage = newChannel.sendMessageEmbeds(RoleMenu.getEmptyEmbed()).complete();

        try {
            RoleMenu menu = new RoleMenu(newMessage.getId(), newChannel.getId(), event.getGuild().getId());
            Wylx.getInstance().getDb().getRoleMenu(newMessage.getId())
                    .setSetting(RoleMenuIdentifiers.ROLE_MENU, menu);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            event.getMessage().reply("Could not create new Role Menu").queue();
        }
    }
}
