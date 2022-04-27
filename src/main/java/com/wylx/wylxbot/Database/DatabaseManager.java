package com.wylx.wylxbot.Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.wylx.wylxbot.Core.WylxEnvConfig;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Represents the Mongo DB and caches servers and users.
 */
public class DatabaseManager {

    private final ConnectionString connectionString;
    private final MongoClient client;
    private final HashMap<String, DiscordServer> serverCache = new HashMap<>();
    private final HashMap<String, DiscordUser> userCache = new HashMap<>();

    public DatabaseManager(WylxEnvConfig config) {
        connectionString = new ConnectionString(config.dbUrl);
        client = getMongoClient();
    }

    private MongoClient getMongoClient() {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("Wylx")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();
        return MongoClients.create(clientSettings);
    }

    /**
     * Retrieve a guild/server from DB.
     *
     * @param serverId Guild ID from Discord
     * @return Discord Server
     */
    public DiscordServer getServer(String serverId) {
        if (!serverCache.containsKey(serverId)) {
            serverCache.put(serverId, new DiscordServer(client, serverId));
        }

        return serverCache.get(serverId);
    }

    /**
     * Retrieve a user from DB.
     *
     * @param userId User ID from Discord
     * @return Discord User
     */
    public DiscordUser getUser(String userId) {
        if (!userCache.containsKey(userId)) {
            userCache.put(userId, new DiscordUser(client, userId));
        }

        return userCache.get(userId);
    }
}
