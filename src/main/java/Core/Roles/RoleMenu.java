package Core.Roles;

import Core.Wylx;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleMenu {

    private final String title;

    private final String guildID;
    private final String messageID;
    private final String channelID;

    private final Message message;

    private final ArrayList<RoleReaction> reactions = new ArrayList<>();

    public RoleMenu(String messageID, String channelID, String guildID) {
        this("Role Selection", messageID, channelID, guildID, null);
        updateMessage();
    }

    public RoleMenu(String title, String messageID, String channelID, String guildID, Map<String, String> roles) {
        this.title = title;
        this.messageID = messageID;
        this.channelID = channelID;
        this.guildID = guildID;

        JDA jda = Wylx.getInstance().getJDA();
        message = jda.getTextChannelById(channelID).retrieveMessageById(messageID).complete();
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

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(this.title);
        StringBuilder string = new StringBuilder("React to get a role!\n\n");
        if (reactions.size() == 0) {
            // TODO: AAAA
            string.append("This list is currently empty. To add roles, please use TODO");
        } else {
            reactions.forEach(reaction -> {
                String line = String.format("%s - %s\n",
                        reaction.emoji().getAsMention(),
                        reaction.role().getAsMention());
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

    public void updateMessage() {
        message.editMessageEmbeds(getEmbed()).queue();
    }

    public Boolean addReaction(RoleReaction newReaction) {
        for (RoleReaction reaction : reactions) {
            if (reaction.role().equals(newReaction.role())) {
                return false;
            }
        }

        reactions.add(newReaction);
        if (newReaction.emoji().isUnicode()) {
            message.addReaction(newReaction.emoji().getAsMention()).queue();
        } else {
            Emote emote = Wylx.getInstance().getJDA().getEmoteById(newReaction.emoji().getId());
            if (emote != null) {
                message.addReaction(emote).queue();
            }
        }
        updateMessage();
        return true;
    }

    public Boolean removeReaction(String name) {
        List<RoleReaction> filtered = reactions.stream().filter(roleReaction -> roleReaction.role().getName().equalsIgnoreCase(name)).toList();
        if (filtered.size() != 1) return false;
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
        return true;
    }


}
