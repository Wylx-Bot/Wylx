package Database;

import org.bson.codecs.Codec;

public interface DiscordIdentifiers {
    String getIdentifier();
    Class<?> getDataType();
    Codec<?> getCodec();
    Object getDefaultValue();
}
