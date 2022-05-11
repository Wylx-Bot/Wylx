package com.wylxbot.wylx.Database.Codecs;

import com.wylxbot.wylx.Core.Util.WylxStats;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class WylxStatsCodec implements Codec<WylxStats> {
    @Override
    public WylxStats decode(BsonReader reader, DecoderContext decoderContext) {
        long commandsProcessed = reader.readInt64("CommandsProcessed");
        double averageCommandTime = reader.readDouble("AverageCommandTime");

        long silentEventsProcessed = reader.readInt64("SilentEventsProcessed");
        double averageSilentEventTime = reader.readDouble("AverageSilentEventTime");

        long noOpsProcessed = reader.readInt64("NoOpsProcessed");
        double averageNoOpTime = reader.readDouble("AverageNoOpTime");

        return new WylxStats(commandsProcessed, averageCommandTime,
                silentEventsProcessed, averageSilentEventTime,
                noOpsProcessed, averageNoOpTime);
    }

    @Override
    public void encode(BsonWriter writer, WylxStats value, EncoderContext encoderContext) {
        writer.writeInt64("CommandsProcessed", value.getCommandsProcessed());
        writer.writeDouble("AverageCommandTime", value.getAverageCommandTime());

        writer.writeInt64("SilentEventsProcessed", value.getSilentEventsProcessed());
        writer.writeDouble("AverageSilentEventTime", value.getAverageSilentEventTime());

        writer.writeInt64("NoOpsProcessed", value.getNoOpsProcessed());
        writer.writeDouble("AverageNoOpTime", value.getAverageNoOpTime());
    }

    @Override
    public Class<WylxStats> getEncoderClass() {
        return WylxStats.class;
    }
}
