package com.wylxbot.wylx.Database.Pojos;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.HashMap;
import java.util.Map;

public class DBServer {
    @BsonId()
    public String serverId;
    public Map<String, Boolean> enabledModules = new HashMap<>();
    public Map<String, Boolean> exceptions = new HashMap<>();
    public int musicVolume = 20;
    public String prefix = "$";

    // Default constructor for POJO Codec
    public DBServer() {}

    public DBServer(String serverId) {
        this.serverId = serverId;
    }
}
