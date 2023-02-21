package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

public class DBUser {
    @BsonId()
    public String userId;
    public String timezone;
    public boolean timezonePrompted;
    public DBUserFightStats fightStats;

    // Default constructor for POJO codec
    public DBUser() {}

    public DBUser(String userId) {
        this.userId = userId;
        this.timezone = "LOL";
        this.timezonePrompted = false;
        this.fightStats = new DBUserFightStats();
    }
}
