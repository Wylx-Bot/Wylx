package com.wylxbot.wylx.Database;

import com.mongodb.client.MongoCollection;
import com.wylxbot.wylx.Core.WylxEnvConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.wylxbot.wylx.Database.Pojos.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseManager {

    private static final String POJOS_PACKAGE = "com.wylxbot.wylx.Database.Pojos";

    private final MongoClient client;
    private final MongoCollection<DBRoleMenu> roleMenuCollection;
    private final MongoCollection<DBServer> serversCollection;
    private final MongoCollection<DBUser> usersCollection;
    private final MongoCollection<DBCommandStats> statsCollection;

    public DatabaseManager(WylxEnvConfig config) {
        ConnectionString connStr = new ConnectionString(config.dbURL);
        client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(connStr)
                .applicationName("Wylx")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS)
                )
                .build());

        // Combine default codec with our POJOs to represent data objects
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().register(POJOS_PACKAGE).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoDatabase database = client.getDatabase("Wylx").withCodecRegistry(pojoCodecRegistry);

        roleMenuCollection = database.getCollection("Role Menus", DBRoleMenu.class);
        serversCollection = database.getCollection("Servers", DBServer.class);
        usersCollection = database.getCollection("Users", DBUser.class);
        statsCollection = database.getCollection("Stats", DBCommandStats.class);
    }

    public void readCluster() {
        System.out.println(" --- EXISTING DATABASES ---");
        for(String mongoDatabase : client.listDatabaseNames()) {
            MongoDatabase database = client.getDatabase(mongoDatabase);
            System.out.println(mongoDatabase + ": ");
            for(String mongoCollection : database.listCollectionNames()) {
                System.out.println("\t" + mongoCollection + ": ");
                for(Document document : database.getCollection(mongoCollection).find()) {
                    System.out.println("\t\t" + document.toJson());
                }
            }

        }
        System.out.println(" --- END OF EXISTING DATABASES ---");
    }

    public DBServer getServer(String serverId) {
        var iter = serversCollection.find(eq("_id", serverId));
        DBServer server = iter.first();
        if (server == null) {
            server = new DBServer(
                    serverId,
                    new HashMap<>(),
                    new HashMap<>(),
                    20,
                    "$");
            serversCollection.insertOne(server);
        }

        return server;
    }

    public DBUser getUser(String userID) {
        var iter = usersCollection.find(eq("_id", userID));
        DBUser user = iter.first();
        if (user == null) {
            user = new DBUser(
                    userID,
                    "LOL",
                    false,
                    new DBUserFightStats(0, 0, 0, 0, 0, 0));
            usersCollection.insertOne(user);
        }

        return user;
    }

    public DBRoleMenu getRoleMenu(String messageID) {
        var iter = roleMenuCollection.find(eq("_id", messageID));
        return iter.first();
    }

    public DBCommandStats getGlobal() {
        var iter = statsCollection.find(eq("_id", "STATS"));
        DBCommandStats stats = iter.first();
        if (stats == null) {
            stats = new DBCommandStats(
                "STATS",
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
            statsCollection.insertOne(stats);
        }

        return stats;
    }
}
