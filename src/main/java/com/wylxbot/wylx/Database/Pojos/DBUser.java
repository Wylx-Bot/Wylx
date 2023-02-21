package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

public record DBUser(
        @BsonId()
        String userId,
        String timezone,
        boolean timezonePrompted,
        DBUserFightStats fightStats
) {}
