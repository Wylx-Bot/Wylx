package com.wylx.wylxbot.Database;

import com.mongodb.client.MongoClient;

/**
 * Represents a discord server in the DB.
 */
public class DiscordServer extends DiscordElement<ServerIdentifiers> {
    private static final String SERVER_SETTINGS_DOC = "Server_Settings";

    protected DiscordServer(MongoClient client, String id) {
        super(client, id, SERVER_SETTINGS_DOC, ServerIdentifiers.values());
    }
}
