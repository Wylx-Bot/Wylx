package com.wylx.wylxbot.Database;

import com.mongodb.client.MongoClient;

/**
 * Represents a discord user in the DB.
 */
public class DiscordUser extends DiscordElement<UserIdentifiers> {
    private static final String SETTINGS_DOC = "User_Settings";

    protected DiscordUser(MongoClient client, String id) {
        super(client, id, SETTINGS_DOC, UserIdentifiers.values());
    }
}
