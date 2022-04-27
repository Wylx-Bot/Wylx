package com.wylx.wylxbot.Database;

import com.wylx.wylxbot.Core.Fight.FightUserStats;
import java.lang.reflect.InvocationTargetException;
import org.bson.codecs.Codec;

/**
 * MongoDB Database settings for a user.
 */
public enum UserIdentifiers implements DiscordIdentifiers {
    Timezone("Timezone", String.class, "LOL"),
    TimezonePrompted("TimezonePrompted", Boolean.class, false),
    FightStats("FightStats", FightUserStats.class, new FightUserStats());

    private final String identifier;
    private final Class<?> dataType;
    private final Object defaultValue;

    UserIdentifiers(String identifier, Class<?> dataType, Object defaultValue) {
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
        if (!(defaultValue instanceof Codec<?>)) {
            return defaultValue;
        } else {
            try {
                return dataType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }
}
