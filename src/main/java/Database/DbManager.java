package Database;

import Core.WylxEnvConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.TimeUnit;

public class DbManager {
    private static final String WYLX_DB = "Wylx";

    private final ConnectionString connectionString;
    private final DbCollection<ServerIdentifiers> guildsCollection;
    private final DbCollection<UserIdentifiers> usersCollection;

    public DbManager(WylxEnvConfig config) {
        connectionString = new ConnectionString(config.dbURL);
        MongoClient client = getMongoClient();
        MongoDatabase db = client.getDatabase(WYLX_DB);
        guildsCollection = new DbCollection<>(db, "Guilds");
        usersCollection = new DbCollection<>(db, "Users");
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

    public DbCollection<ServerIdentifiers> getServerCollection() {
        return guildsCollection;
    }

    public DbCollection<UserIdentifiers> getUserCollection(){
        return usersCollection;
    }
}
