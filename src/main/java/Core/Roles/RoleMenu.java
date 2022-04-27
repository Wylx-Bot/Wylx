package Core.Roles;

import Core.Wylx;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleMenu {

    private String title;

    private final String guildID;
    private final String messageID;
    private final String channelID;

    private final Message message;

    private final ArrayList<RoleReaction> reactions = new ArrayList<>();

    public RoleMenu(String messageID, String channelID, String guildID) throws Exception {
        this("Role Selection", messageID, channelID, guildID, null);
        updateMessage();
    }

    public RoleMenu(String title, String messageID, String channelID, String guildID, Map<String, String> roles) throws Exception {
        this.title = title;
        this.messageID = messageID;
        this.channelID = channelID;
        this.guildID = guildID;

        JDA jda = Wylx.getInstance().getJDA();
        TextChannel channel = jda.getTextChannelById(channelID);
        if (channel == null) {
            throw new Exception("Channel does not exist");
        }

        try {
            message = channel.retrieveMessageById(messageID).complete();
        } catch (ErrorResponseException e) {
            throw new Exception("Message does not exist");
        }

        if (roles == null) {
            return;
        }

        roles.forEach((roleID, emojiID) -> {
            Role role = jda.getRoleById(roleID);
            // Remove if invalid
            if (role == null) {
                return;
            }

            Emoji emoji;
            try {
                Emote emote = jda.getEmoteById(emojiID);
                if (emote == null) {
                    return;
                } else {
                    emoji = Emoji.fromEmote(emote);
                }
            } catch (NumberFormatException e) {
                emoji = Emoji.fromUnicode(emojiID);
            }

            reactions.add(new RoleReaction(role, emoji));
        });

    }

    public static MessageEmbed getEmptyEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Role Selection");
        builder.setDescription("React to get a role!\n\nThis list is currently empty. To add roles, please use TODO");
        return builder.build();
    }

    public MessageEmbed getEmbed(boolean mentionRoles) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(this.title);
        StringBuilder string = new StringBuilder("React to get a role!\n\n");
        if (reactions.size() == 0) {
            String prefix = Wylx.getInstance().getDb().getServer(guildID).getSetting(ServerIdentifiers.Prefix);
            string.append("This list is currently empty. To add roles, please use the `");
            string.append(prefix);
            string.append("modifyRoleMenu` command");
        } else {
            reactions.forEach(reaction -> {
                String roleStr = mentionRoles ? reaction.role().getAsMention() : reaction.role().getName();
                String line = String.format("%s - %s\n",
                        reaction.emoji().getAsMention(),
                        roleStr);
                string.append(line);
            });
        }

        builder.setDescription(string.toString());
        builder.setFooter("Message ID: " + this.messageID);
        return builder.build();
    }

    public String getTitle() {
        return title;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getChannelID() {
        return channelID;
    }

    public List<RoleReaction> getReactions() {
        return reactions;
    }

    public void setTitle(String title) {
        this.title = title;
        updateMessage();
    }

    private void updateMessage() {
        message.editMessageEmbeds(getEmbed(true)).queue();
    }

    public void addReaction(RoleReaction newReaction) throws Exception {
        for (RoleReaction reaction : reactions) {
            if (reaction.role().equals(newReaction.role())) {
                throw new Exception("Role already exists in menu");
            } else if (reaction.emoji().equals(newReaction.emoji())) {
                throw new Exception("Emoji already being used in menu");
            }
        }

        reactions.add(newReaction);
        if (newReaction.emoji().isUnicode()) {
            message.addReaction(newReaction.emoji().getAsMention()).queue();
        } else {
            Emote emote = Wylx.getInstance().getJDA().getEmoteById(newReaction.emoji().getId());
            if (emote != null) {
                message.addReaction(emote).queue();
            } else {
                throw new Exception("Emote does not exist");
            }
        }
        updateMessage();
    }

    public void removeReaction(String name) throws Exception {
        List<RoleReaction> filtered = reactions.stream().filter(roleReaction -> roleReaction.role().getName().equalsIgnoreCase(name)).toList();
        if (filtered.size() != 1) {
            throw new Exception("Could not find role to remove");
        }
        RoleReaction reaction = filtered.get(0);
        reactions.remove(reaction);
        if (reaction.emoji().isUnicode()) {
            message.clearReactions(reaction.emoji().getAsMention()).queue();
        } else {
            Emote emote = Wylx.getInstance().getJDA().getEmoteById(reaction.emoji().getId());
            if (emote != null) {
                message.clearReactions(emote).queue();
            }
        }

        updateMessage();
    }

    public Role getReactionFromEmote(Emoji emoji) {
        for (RoleReaction reaction : reactions) {
            if (reaction.emoji().equals(emoji)) {
                return reaction.role();
            }
        }
        return null;
    }


}
