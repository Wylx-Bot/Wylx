package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.Map;

public record DBServer(
        @BsonId
        String serverId,
        Map<String, Boolean> enabledModules,
        Map<String, Boolean> exceptions,
        int musicVolume,
        String prefix
) {}
