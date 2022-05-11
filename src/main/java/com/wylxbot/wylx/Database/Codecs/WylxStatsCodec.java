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
        long averageCommandTime = reader.readInt64("AverageCommandTime");

        long silentEventsProcessed = reader.readInt64("SilentEventsProcessed");
        long averageSilentEventTime = reader.readInt64("AverageSilentEventTime");

        long noOpsProcessed = reader.readInt64("NoOpsProcessed");
        long averageNoOpTime = reader.readInt64("AverageNoOpTime");

        return new WylxStats(commandsProcessed, averageCommandTime,
                silentEventsProcessed, averageSilentEventTime,
                noOpsProcessed, averageNoOpTime);
    }

    @Override
    public void encode(BsonWriter writer, WylxStats value, EncoderContext encoderContext) {
        writer.writeInt64("CommandsProcessed", value.getCommandsProcessed());
        writer.writeInt64("AverageCommandTime", value.getAverageCommandTime());

        writer.writeInt64("SilentEventsProcessed", value.getSilentEventsProcessed());
        writer.writeInt64("AverageSilentEventTime", value.getAverageSilentEventTime());

        writer.writeInt64("NoOpsProcessed", value.getNoOpsProcessed());
        writer.writeInt64("AverageNoOpTime", value.getAverageNoOpTime());
    }

    @Override
    public Class<WylxStats> getEncoderClass() {
        return WylxStats.class;
    }
}
