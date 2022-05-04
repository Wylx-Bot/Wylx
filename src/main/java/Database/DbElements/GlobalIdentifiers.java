package Database.DbElements;

import Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

public enum GlobalIdentifiers implements DiscordIdentifiers {
    ;

    public final String identifier;
    public final Class<?> dataType;
    public final Supplier<Object> defaultSupplier;

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Class<?> getDataType() {
        return null;
    }

    @Override
    public Codec<?> getCodec() {
        return null;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }
}
