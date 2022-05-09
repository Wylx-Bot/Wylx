package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.util.function.Supplier;

public enum GlobalIdentifiers implements DiscordIdentifiers {
    CommandsProcessed("Commands_Processed", Integer.class, () -> 0),
    AverageCommandTime("Average_Command_Time", Long.class, () -> 0L),
    SilentEventsProcessed("Silent_Events_Processed", Integer.class, () -> 0),
    AverageSilentEventTime("Average_Silent_Event_Time", Long.class, () -> 0L),
    NoOpsProcessed("NoOps_Processed", Integer.class, () -> 0),
    AverageNoOpTime("Average_NoOp_Time", Long.class, () -> 0L);

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
