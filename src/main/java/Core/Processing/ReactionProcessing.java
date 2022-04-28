package Core.Processing;

import Commands.Roles.RolesUtil.RoleMenu;
import Core.Wylx;
import Database.DiscordRoleMenu;
import Database.RoleMenuIdentifiers;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionProcessing extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Role role = checkReactionForMenu(event);
        if (role != null) {
            Member user = event.retrieveMember().complete();
            Guild guild = event.getGuild();
            guild.addRoleToMember(user, role).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Role role = checkReactionForMenu(event);
        if (role != null) {
            Member user = event.retrieveMember().complete();
            Guild guild = event.getGuild();
            guild.removeRoleFromMember(user, role).queue();
        }
    }

    private Role checkReactionForMenu(@NotNull GenericMessageReactionEvent event) {
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        Member selfMember = event.getGuild().getSelfMember();

        // Check if it's a reaction to a message Wylx sent
        if (!msg.getAuthor().equals(selfMember.getUser())) {
            return null;
        }

        // Check for menu
        DiscordRoleMenu roleDb = Wylx.getInstance().getDb().getRoleMenu(event.getMessageId());
        RoleMenu menu = roleDb.getSettingOrNull(RoleMenuIdentifiers.ROLE_MENU);
        if (menu == null) {
            return null;
        }

        Emoji emoji;
        if (event.getReactionEmote().isEmoji()) {
            emoji = Emoji.fromUnicode(event.getReactionEmote().getEmoji());
        } else {
            emoji = Emoji.fromEmote(event.getReactionEmote().getEmote());
        }

        return menu.getReactionFromEmote(emoji);
    }
}
