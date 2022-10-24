package com.wylxbot.wylx.Core.Processing;

import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleMenu;
import com.wylxbot.wylx.Wylx;
import com.wylxbot.wylx.Database.DbElements.DiscordRoleMenu;
import com.wylxbot.wylx.Database.DbElements.RoleMenuIdentifiers;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

public class ReactionProcessing extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Role role = checkReactionForMenu(event);
        if (role != null) {
            Member user = event.retrieveMember().complete();
            Guild guild = event.getGuild();
            try {
                guild.addRoleToMember(user, role).complete();
            } catch (ErrorResponseException e) {
                if (e.getErrorResponse() == ErrorResponse.UNKNOWN_ROLE) {
                    removeRole(event, role);
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Role role = checkReactionForMenu(event);
        if (role != null) {
            Member user = event.retrieveMember().complete();
            Guild guild = event.getGuild();
            try {
                guild.removeRoleFromMember(user, role).complete();
            } catch (ErrorResponseException e) {
                if (e.getErrorResponse() == ErrorResponse.UNKNOWN_ROLE) {
                    removeRole(event, role);
                }
            }
        }
    }

    private void removeRole(@NotNull GenericMessageReactionEvent event, Role roleToRemove) {
        DiscordRoleMenu roleDb = Wylx.getInstance().getDb().getRoleMenu(event.getMessageId());
        RoleMenu menu = roleDb.getSettingOrNull(RoleMenuIdentifiers.ROLE_MENU);
        menu.removeReaction(roleToRemove.getName());
    }

    private Role checkReactionForMenu(@NotNull GenericMessageReactionEvent event) {
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        User selfUser = event.getJDA().getSelfUser();

        // Check if it's a reaction to a message Wylx sent
        if (!msg.getAuthor().equals(selfUser)) {
            return null;
        }

        // Don't give roles to Wylx
        if (event.getUserIdLong() == selfUser.getIdLong()) {
            return null;
        }

        // Check for menu
        DiscordRoleMenu roleDb = Wylx.getInstance().getDb().getRoleMenu(event.getMessageId());
        RoleMenu menu = roleDb.getSettingOrNull(RoleMenuIdentifiers.ROLE_MENU);
        if (menu == null) {
            return null;
        }

        EmojiUnion emoji = event.getEmoji();
        return menu.getReactionFromEmote(emoji);
    }
}
