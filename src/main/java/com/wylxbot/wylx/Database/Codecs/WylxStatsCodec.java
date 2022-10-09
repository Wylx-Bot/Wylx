package com.wylxbot.wylx.Database.Codecs;

import com.wylxbot.wylx.Core.Util.WylxStats;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class WylxStatsCodec implements Codec<WylxStats> {

    public static final String COMMANDS_PROCESSED_KEY = "CommandsProcessed";
    public static final String AVERAGE_COMMAND_TIME_KEY = "AverageCommandTime";
    public static final String SILENT_EVENTS_PROCESSED_KEY = "SilentEventsProcessed";
    public static final String AVERAGE_SILENT_EVENT_TIME_KEY = "AverageSilentEventTime";
    public static final String NO_OPS_PROCESSED_KEY = "NoOpsProcessed";
    public static final String AVERAGE_NO_OP_TIME_KEY = "AverageNoOpTime";

    @Override
    public WylxStats decode(BsonReader reader, DecoderContext decoderContext) {
        long commandsProcessed = reader.readInt64(COMMANDS_PROCESSED_KEY);
        double averageCommandTime = reader.readDouble(AVERAGE_COMMAND_TIME_KEY);

        long silentEventsProcessed = reader.readInt64(SILENT_EVENTS_PROCESSED_KEY);
        double averageSilentEventTime = reader.readDouble(AVERAGE_SILENT_EVENT_TIME_KEY);

        long noOpsProcessed = reader.readInt64(NO_OPS_PROCESSED_KEY);
        double averageNoOpTime = reader.readDouble(AVERAGE_NO_OP_TIME_KEY);

        return new WylxStats(commandsProcessed, averageCommandTime,
                silentEventsProcessed, averageSilentEventTime,
                noOpsProcessed, averageNoOpTime);
    }

    @Override
    public void encode(BsonWriter writer, WylxStats value, EncoderContext encoderContext) {
        writer.writeInt64(COMMANDS_PROCESSED_KEY, value.getCommandsProcessed());
        writer.writeDouble(AVERAGE_COMMAND_TIME_KEY, value.getAverageCommandTime());

        writer.writeInt64(SILENT_EVENTS_PROCESSED_KEY, value.getSilentEventsProcessed());
        writer.writeDouble(AVERAGE_SILENT_EVENT_TIME_KEY, value.getAverageSilentEventTime());

        writer.writeInt64(NO_OPS_PROCESSED_KEY, value.getNoOpsProcessed());
        writer.writeDouble(AVERAGE_NO_OP_TIME_KEY, value.getAverageNoOpTime());
    }

    @Override
    public Class<WylxStats> getEncoderClass() {
        return WylxStats.class;
    }
}
