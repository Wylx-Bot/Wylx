package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;

public record DBRoleMenu(
        @BsonId()
        ObjectId messageId,
        String channelId,
        String guildId,
        String title,
        List<DBRoleMenuRole> roles
) {}
