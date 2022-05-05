package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Database.DiscordElement;
import com.mongodb.client.MongoClient;

public class DiscordGlobal extends DiscordElement<GlobalIdentifiers> {
    public DiscordGlobal(MongoClient client) {
        super(client, "WylxGlobal", "GlobalSettings", GlobalIdentifiers.values());
    }
}
