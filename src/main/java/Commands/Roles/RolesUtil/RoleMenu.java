package Commands.Roles.RolesUtil;

import Core.Wylx;
import Database.DbElements.ServerIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleMenu implements Codec<RoleMenu> {

    private String title;

    private final String guildID;
    private final String messageID;
    private final String channelID;

    private final Message message;

    private final ArrayList<RoleReaction> reactions = new ArrayList<>();

    /**
     * This constructor is only used for decode. Decode will fill out the fields and return a new RoleMenu.
     * DO NOT USE!
     */
    public RoleMenu() {
        this.guildID = "DECODE";
        this.messageID = "DECODE";
        this.channelID = "THIS IS DUMB";
        this.message = null;
    }

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
            string.append("This list is currently empty. To add roles, please use\n`");
            string.append(prefix);
            string.append("modifyRoleMenu ");
            string.append(this.messageID);
            string.append("`\nto modify this menu.");
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

    private final static String GUILD_ID = "Guild ID";
    private final static String CHANNEL_ID = "Channel ID";
    private final static String MESSAGE_ID = "_id"; // Primary key = message id
    private final static String TITLE = "Title";
    private final static String ROLES = "Roles";

    private final static String ROLE_ID = "Role ID";
    private final static String EMOJI_ID = "Emoji ID";

    @Override
    public RoleMenu decode(BsonReader reader, DecoderContext decoderContext) {
        String guildID = "";
        String channelID = "";
        String messageID = "";
        String title = "";
        Map<String, String> roles = new HashMap<>();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            switch (fieldName) {
                case GUILD_ID -> guildID = reader.readString();
                case CHANNEL_ID -> channelID = reader.readString();
                case MESSAGE_ID -> messageID = reader.readString();
                case TITLE -> title = reader.readString();
                case ROLES -> {
                    reader.readStartArray();

                    while (reader.readBsonType() == BsonType.DOCUMENT) {
                        reader.readStartDocument();
                        String roleID = reader.readString(ROLE_ID);
                        String emojiID = reader.readString(EMOJI_ID);
                        roles.put(roleID, emojiID);
                        reader.readEndDocument();
                    }

                    reader.readEndArray();
                }
            }
        }

        try {
            return new RoleMenu(title, messageID, channelID, guildID, roles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void encode(BsonWriter writer, RoleMenu value, EncoderContext encoderContext) {
        writer.writeString(MESSAGE_ID, value.getMessageID());
        writer.writeString(CHANNEL_ID, value.getChannelID());
        writer.writeString(GUILD_ID, value.getGuildID());
        writer.writeString(TITLE, value.getTitle());

        // Roles
        writer.writeName(ROLES);
        writer.writeStartArray();
        List<RoleReaction> reactions = value.getReactions();
        reactions.forEach((roleReaction -> {
            writer.writeStartDocument();
            writer.writeString(ROLE_ID, roleReaction.role().getId());
            Emoji emoji = roleReaction.emoji();
            // Unicode emojis have ID = 0, and come with discord
            if (emoji.isUnicode()) {
                writer.writeString(EMOJI_ID, emoji.getName());
            } else {
                writer.writeString(EMOJI_ID, emoji.getId());
            }
            writer.writeEndDocument();
        }));

        writer.writeEndArray();
    }

    @Override
    public Class<RoleMenu> getEncoderClass() {
        return RoleMenu.class;
    }
}
