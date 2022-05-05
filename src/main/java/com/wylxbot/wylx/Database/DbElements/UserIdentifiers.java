package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Database.Codecs.FightUserStatsCodec;
import com.wylxbot.wylx.Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

import com.wylxbot.wylx.Commands.Fight.Util.FightUserStats;

public enum UserIdentifiers implements DiscordIdentifiers {
    Timezone("Timezone", String.class, () -> "LOL"),
    TimezonePrompted("TimezonePrompted", Boolean.class, () -> false),
    FightStats("FightStats", FightUserStats.class, FightUserStats::new, new FightUserStatsCodec());

    private final String identifier;
    private final Class<?> dataType;
    private final Supplier<Object> defaultSupplier;
    private final Codec<?> codec;

    UserIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue) {
        this(identifier, dataType, defaultValue, null);
    }

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
