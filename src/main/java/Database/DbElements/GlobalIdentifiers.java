package Database.DbElements;

import Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

public enum GlobalIdentifiers implements DiscordIdentifiers {
    CommandsProcessed("Commands_Processed", Integer.class, () -> 0, null);

    public final String identifier;
    public final Class<?> dataType;
    public final Supplier<Object> defaultSupplier;
    public final Codec<?> codec;

    GlobalIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultSupplier, Codec<?> codec){
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultSupplier = defaultSupplier;
        this.codec = codec;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    @Override
    public Codec<?> getCodec() {
        return codec;
    }

    @Override
    public Object getDefaultValue() {
        return defaultSupplier.get();
    }
}
