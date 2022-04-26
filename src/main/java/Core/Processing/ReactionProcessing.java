package Core.Processing;

import Core.Roles.RoleMenu;
import Core.Wylx;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionProcessing extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        Member selfMember = event.getGuild().getSelfMember();

        // Check if it's a reaction to a message Wylx sent
        if (!msg.getAuthor().equals(selfMember.getUser())) {
            return;
        }

        // Check for menu
        RoleMenu menu = Wylx.getInstance().getDb().getRoleMenu(event.getMessageId());
        if (menu == null) {
            return;
        }

        Emoji emoji;
        if (event.getReactionEmote().isEmoji()) {
            emoji = Emoji.fromUnicode(event.getReactionEmote().getEmoji());
        } else {
            emoji = Emoji.fromEmote(event.getReactionEmote().getEmote());
        }

        Role role = menu.getReactionFromEmote(emoji);
        if (role == null) {
            return;
        }

        Member user = event.retrieveMember().complete();
        Guild guild = event.getGuild();
        guild.addRoleToMember(user, role).queue();
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        Member selfMember = event.getGuild().getSelfMember();

        // Check if it's a reaction to a message Wylx sent
        if (!msg.getAuthor().equals(selfMember.getUser())) {
            return;
        }

        // Check for menu
        RoleMenu menu = Wylx.getInstance().getDb().getRoleMenu(event.getMessageId());
        if (menu == null) {
            return;
        }

        Emoji emoji;
        if (event.getReactionEmote().isEmoji()) {
            emoji = Emoji.fromUnicode(event.getReactionEmote().getEmoji());
        } else {
            emoji = Emoji.fromEmote(event.getReactionEmote().getEmote());
        }

        Role role = menu.getReactionFromEmote(emoji);
        if (role == null) {
            return;
        }

        Member user = event.retrieveMember().complete();
        Guild guild = event.getGuild();
        guild.removeRoleFromMember(user, role).queue();
    }
}
