package Database;

import org.bson.codecs.Codec;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import Core.Fight.FightUserStats;

public enum UserIdentifiers implements DiscordIdentifiers{
    Timezone("Timezone", String.class, () -> "LOL"),
    TimezonePrompted("TimezonePrompted", Boolean.class, () -> false),
    FightStats("FightStats", FightUserStats.class, FightUserStats::new);

    private final String identifier;
    private final Class<?> dataType;
    private final Supplier<Object> defaultValue;

    UserIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue){
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
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
    public Object getDefaultValue() {
        return defaultValue.get();
    }
}
