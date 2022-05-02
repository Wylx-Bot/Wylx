package Database;

import org.bson.codecs.Codec;

import java.util.function.Supplier;

import Commands.Fight.Util.FightUserStats;

public enum UserIdentifiers implements DiscordIdentifiers{
    Timezone("Timezone", String.class, () -> "LOL", null),
    TimezonePrompted("TimezonePrompted", Boolean.class, () -> false, null),
    FightStats("FightStats", FightUserStats.class, FightUserStats::new, null);

    private final String identifier;
    private final Class<?> dataType;
    private final Supplier<Object> defaultSupplier;
    private final Codec<?> codec;

    UserIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue, Codec<?> codec) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultSupplier = defaultValue;
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
    public Codec<?> getCodec(){
        return codec;
    }

    @Override
    public Object getDefaultValue() {
        return defaultSupplier.get();
    }
}
