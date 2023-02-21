package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

public record DBCommandStats(
        @BsonId()
        String id,
        long commandsProcessed,
        double averageCommandTime,
        long silentEventsProcessed,
        double averageSilentEventTime,
        long noOpsProcessed,
        double averageNoOpTime
) {}
