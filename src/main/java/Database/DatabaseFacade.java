package Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DatabaseFacade {

    private static ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass&ssl=false");
    private static MongoClient client = getMongoClient();

    public DatabaseFacade() {
    }

    private static MongoClient getMongoClient() {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("Wylx")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();
        return MongoClients.create(clientSettings);
    }

    public static void readCluster() {
        MongoClient mongoClient = getMongoClient();
        System.out.println(" --- EXISTING DATABASES ---");
        for(String mongoDatabase : mongoClient.listDatabaseNames()) {
            MongoDatabase database = mongoClient.getDatabase(mongoDatabase);
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

    public static ArrayList<DiscordServer> getExistingServers() {
        MongoClient mongoClient = getMongoClient();
        ArrayList<DiscordServer> servers = new ArrayList<>();
        mongoClient.listDatabaseNames().forEach(name -> servers.add(new DiscordServer(mongoClient, name)));
        return servers;
    }

    public static DiscordServer newServer(String severId) {
        return new DiscordServer(client, severId);
    }
}
