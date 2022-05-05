package com.wylxbot.wylx.Database.DbElements;

import com.wylxbot.wylx.Database.DiscordElement;
import com.mongodb.client.MongoClient;

import static com.mongodb.client.model.Filters.exists;

public class DiscordServer extends DiscordElement<ServerIdentifiers> {
    private static final String SERVER_SETTINGS_DOC = "Server_Settings";

    public DiscordServer(MongoClient client, String id) {
        super(client, id, SERVER_SETTINGS_DOC, ServerIdentifiers.values());
    }
}
