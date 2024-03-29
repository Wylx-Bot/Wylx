package com.wylxbot.wylx.Database.Codecs;

import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleMenu;
import com.wylxbot.wylx.Commands.Roles.RolesUtil.RoleReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleMenuCodec implements Codec<RoleMenu> {

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
            EmojiUnion emoji = roleReaction.emoji();
            writer.writeString(EMOJI_ID, emoji.getFormatted());
            // Unicode emojis have ID = 0, and come with discord
            if (emoji.getType() == Emoji.Type.UNICODE) {
                writer.writeString(EMOJI_ID, emoji.getName());
            } else {
                writer.writeString(EMOJI_ID, emoji.asCustom().getId());
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
