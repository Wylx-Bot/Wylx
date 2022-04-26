package Core.Roles;

import Core.Wylx;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleMenu {

    private final String title;

    private final String guildID;
    private final String messageID;
    private final String channelID;

    private final ArrayList<RoleReaction> reactions = new ArrayList<>();

    public RoleMenu(String messageID, String channelID, String guildID) {
        this("Role Selection", messageID, channelID, guildID, null);
    }

    public RoleMenu(String title, String messageID, String channelID, String guildID, Map<String, String> roles) {
        this.title = title;
        this.messageID = messageID;
        this.channelID = channelID;
        this.guildID = guildID;

        if (roles == null) {
            return;
        }

        JDA jda = Wylx.getInstance().getJDA();
        roles.forEach((roleID, emojiID) -> {
            Role role = jda.getRoleById(roleID);
            Emote emote = jda.getEmoteById(emojiID);
            Emoji emoji;
            if (emote == null) {
                emoji = Emoji.fromUnicode(emojiID);
            } else {
                emoji = Emoji.fromEmote(emote);
            }

            reactions.add(new RoleReaction(role, emoji));
        });
    }

    public static MessageEmbed getEmbed(String title) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(title);
        StringBuilder string = new StringBuilder("React to get a role!\n\n");

        string.append("To edit, please use TODO");
        builder.setDescription(string.toString());
        return builder.build();
    }

    public static MessageEmbed getEmptyEmbed() {
        return getEmbed("Role Selection");
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

    public void addReaction(RoleReaction reaction) {
        reactions.add(reaction);
    }


}
