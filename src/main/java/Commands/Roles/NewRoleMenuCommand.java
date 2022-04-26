package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Roles.RoleMenu;
import Core.Wylx;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class NewRoleMenuCommand extends ServerCommand {
    public NewRoleMenuCommand() {
        super("newrolemenu", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR, "Create new role menu");
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

        Message newMessage = newChannel.sendMessage("A").complete();
        RoleMenu menu = new RoleMenu(newMessage.getId(), newChannel.getId(), event.getGuild().getId());
        Wylx.getInstance().getDb().setRoleMenu(menu);
    }
}
