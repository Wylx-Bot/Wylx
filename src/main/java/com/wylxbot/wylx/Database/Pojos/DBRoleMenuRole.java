package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

public record DBRoleMenuRole(
        @BsonId()
        String roleId,
        boolean isUnicode,
        String emojiStr
) {}
