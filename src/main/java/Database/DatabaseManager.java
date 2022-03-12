package Database;

import Core.WylxEnvConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private final ConnectionString connectionString;
    private final MongoClient client;
    private final Map<String,DiscordServer> serverDBs = new HashMap<>();

    public DatabaseManager(WylxEnvConfig config) {
        connectionString = new ConnectionString(config.dbURL);
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

    public ArrayList<DiscordServer> getExistingServers() {
        ArrayList<DiscordServer> servers = new ArrayList<>();
        client.listDatabaseNames().forEach(name -> servers.add(new DiscordServer(client, name)));
        return servers;
    }

    public DiscordServer getServer(String severId) {
        if (serverDBs.containsKey(severId)) {
            return serverDBs.get(severId);
        }


        return new DiscordServer(client, severId);
    }
}
