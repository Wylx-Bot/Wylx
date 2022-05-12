package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Core.Util.WylxStats;
import com.wylxbot.wylx.Database.Codecs.WylxStatsCodec;
import com.wylxbot.wylx.Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

public enum GlobalIdentifiers implements DiscordIdentifiers {
    BotStats("Wylx_Stats", WylxStats.class, () -> new WylxStats(0, 0, 0, 0, 0, 0), new WylxStatsCodec()),
    DND_Spells_CSV("DND_Spells_CSV", String.class, () -> "If you're seeing this, you did it wrong");

    public final String identifier;
    public final Class<?> dataType;
    public final Supplier<Object> defaultSupplier;
    public final Codec<?> codec;

    GlobalIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultSupplier){
        this(identifier, dataType, defaultSupplier, null);
    }

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
