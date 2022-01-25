package Database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class Setup {

    public static MongoClient getMongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass&ssl=false");
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("Wylx")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();


        MongoClient mongoClient = MongoClients.create(clientSettings);

        return mongoClient;
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

    public static void main(String[] args) {
        DiscordServer example = new DiscordServer(getMongoClient(), "ExampleServer");
        System.out.println("Music Volume: " + example.getMusicVolume());
        example.setMusicVolume(100);
        System.out.println("Music Volume: " + example.getMusicVolume());
        example.setMusicVolume(0);
        System.out.println("Music Volume: " + example.getMusicVolume());
        example.setMusicVolume(50);
        System.out.println("Music Volume: " + example.getMusicVolume());

    }
}
