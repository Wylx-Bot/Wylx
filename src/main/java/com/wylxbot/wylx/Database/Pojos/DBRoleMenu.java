package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public class DBRoleMenu {
    @BsonId()
    public String messageId;
    public String channelId;
    public String guildId;
    public String title;
    public Map<String, DBRoleMenuRole> roles;

    public DBRoleMenu() {};
}
