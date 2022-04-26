package Database;

import Core.Roles.RoleMenu;
import Core.WylxEnvConfig;
import Database.DbElements.DiscordServer;
import Database.DbElements.DiscordUser;
import Database.Codecs.RoleMenuCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.exists;

public class DatabaseManager {

    private static final String ROLE_MENU_COLLECTION_KEY = "Role Menus";
    private static final String GLOBAL_DB_KEY = "WylxGlobals";

    private final ConnectionString connectionString;
    private final MongoClient client;
    private final HashMap<String, DiscordServer> serverCache = new HashMap<>();
    private final HashMap<String, DiscordUser> userCache = new HashMap<>();
    private final HashMap<String, RoleMenu> roleMenuCache = new HashMap<>();

    private final MongoCollection<RoleMenu> roleMenuCollection;

    public DatabaseManager(WylxEnvConfig config) {
        connectionString = new ConnectionString(config.dbURL);
        client = getMongoClient();
        MongoDatabase globalSettings = client.getDatabase(GLOBAL_DB_KEY);

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new RoleMenuCodec()),
                MongoClientSettings.getDefaultCodecRegistry());

        roleMenuCollection = globalSettings.getCollection(ROLE_MENU_COLLECTION_KEY, RoleMenu.class).withCodecRegistry(codecRegistry);
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

    public DiscordServer getServer(String serverId) {
        if(!serverCache.containsKey(serverId))
            serverCache.put(serverId, new DiscordServer(client, serverId));

        return serverCache.get(serverId);
    }

    public DiscordUser getUser(String userID) {
        if(!userCache.containsKey(userID))
            userCache.put(userID, new DiscordUser(client, userID));

        return userCache.get(userID);
    }

    public RoleMenu getRoleMenu(String messageID) {
        // Check cache first, then DB
        if (!roleMenuCache.containsKey(messageID)) {
            RoleMenu menu = roleMenuCollection.find(Filters.eq(messageID)).first();

            // Menu does not exist :(
            if (menu == null) {
                return null;
            }

            roleMenuCache.put(messageID, menu);
        }

        return roleMenuCache.get(messageID);
    }

    public void setRoleMenu(RoleMenu menu) {
        RoleMenu oldMenu = roleMenuCollection.find(Filters.eq(menu.getMessageID())).first();
        if(oldMenu != null)
            roleMenuCollection.deleteOne(Filters.eq(menu.getMessageID()));
        roleMenuCollection.insertOne(menu);
    }
}
