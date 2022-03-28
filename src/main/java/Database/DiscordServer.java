package Database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.*;

import static com.mongodb.MongoNamespace.checkDatabaseNameValidity;
import static com.mongodb.client.model.Filters.exists;

public class DiscordServer{
    private static final String SERVER_SETTINGS_DOC = "Server_Settings";
    private static final String USER_SETTINGS_DOC = "User_Settings";

    private final String _id;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> settingsCollection;
    private final MongoCollection<Document> userCollection;

    public DiscordServer(MongoClient mongoClient, String databaseName) {
        try {
            checkDatabaseNameValidity(databaseName);
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal Database Name: " + databaseName);
        }
        mongoDatabase = mongoClient.getDatabase(databaseName);
        _id = databaseName;
        settingsCollection = getSettingsCollection();
        userCollection = getUsersCollection();

        if (settingsCollection.countDocuments() == 0) {
            initializeSettings();
        }
    }

    /**
     * Returns a string representation of the Server.
     * The Format is
     * <blockquote>
     *       <pre>
     * * _id
     * *   CollectionName:
     * *       DocumentJSON
     * *       DocumentJSON
     * *   ...
     * * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(_id + "\n");
        for(String mongoCollection : mongoDatabase.listCollectionNames()) {
            out.append("\t").append(mongoCollection).append(": \n");
            for(Document document : mongoDatabase.getCollection(mongoCollection).find()) {
                out.append("\t\t").append(document.toJson()).append("\n");
            }
        }
        return out.toString();
    }

    private MongoCollection<Document> getSettingsCollection() {
        for (String name : mongoDatabase.listCollectionNames()) {
            if (name.equals(SERVER_SETTINGS_DOC)) {
                return mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
            }
        }
        mongoDatabase.createCollection(SERVER_SETTINGS_DOC);
        return mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
    }

    public void initializeSettings() {
        for(ServerIdentifiers d : ServerIdentifiers.values()) {
            setSetting(d, d.defaultValue);
        }
    }

    /** Returns the setting value in the first doc with a setting of that name or null if no such setting exists
     * @param identifier the preset identifier for a server setting
     * @return An object of whatever type is stored by MongoDB or null if none exists yet
     */
    @SuppressWarnings("unchecked")
    public <T> T getSetting(ServerIdentifiers identifier) {
        Document settingDoc = settingsCollection.find(exists(identifier.identifier)).first();
        if(settingDoc == null)
            return (T) identifier.defaultValue;
        return settingDoc.get(identifier.identifier, (T) identifier.defaultValue);
    }

    /** Sets a setting in mongoDB
     * @param identifier the preset identifier for a server setting
     * @param data the data for the setting being set, note this must match the type
     */
    public void setSetting(ServerIdentifiers identifier, Object data) {
        if (identifier.dataType.cast(data) == null)
            throw new IllegalArgumentException("Identifier data type mismatch");
        Document settingDoc = settingsCollection.find(exists(identifier.identifier)).first();
        if(settingDoc == null)
            settingDoc = new Document().append(identifier.identifier, data);
        else {
            settingsCollection.deleteOne(exists(identifier.identifier));
            settingDoc.put(identifier.identifier, data);
        }
        settingsCollection.insertOne(settingDoc);
    }

    /** Removes a setting from mongoDB
     * @param identifier the preset identifier for a server setting
     */
    public void removeSetting(ServerIdentifiers identifier) {
        Document settingDoc = settingsCollection.find(exists(identifier.identifier)).first();
        if(settingDoc == null)
            return;
        settingsCollection.deleteOne(exists(identifier.identifier));
    }

    private MongoCollection<Document> getUsersCollection() {
        for (String name : mongoDatabase.listCollectionNames()) {
            if (name.equals(USER_SETTINGS_DOC)) {
                return mongoDatabase.getCollection(USER_SETTINGS_DOC);
            }
        }
        mongoDatabase.createCollection(USER_SETTINGS_DOC);
        return mongoDatabase.getCollection(USER_SETTINGS_DOC);
    }

    public Map<String, Map<String, String>> getUsers() {
        Map<String, Map<String, String>> users = new HashMap<>();
        for(Document user : userCollection.find()) {
            Map<String, String> data = new HashMap<>();
            for(String k : user.keySet()) {
                data.put(k, user.get(k).toString());
            }
            users.put(data.get(UserIdentifiers.DiscordUser.identifier), data);
        }
        return users;
    }

    public void addUser(String discordTag, Map<String, String> data) {
        if(getUser(discordTag) != null)
            removeUser(discordTag);
        Document user = new Document();
        user.append(UserIdentifiers.DiscordUser.identifier, discordTag);
        for(String k : data.keySet()) {
            user.append(k, data.get(k));
        }
        userCollection.insertOne(user);
    }

    public Map<String, String> getUser(String discordTag) {
        Map<String, String> data = new HashMap<>();
        BasicDBObject matchQuery = new BasicDBObject();
        matchQuery.put(UserIdentifiers.DiscordUser.identifier, discordTag);
        Document user =  userCollection.find(matchQuery).first();
        if(user == null) return null;
        for(String k : user.keySet()) {
            data.put(k, user.get(k).toString());
        }
        return data;
    }

    public void removeUser(String discordTag) {
        BasicDBObject matchQuery = new BasicDBObject();
        matchQuery.put("Discord_Tag", discordTag);
        userCollection.deleteOne(matchQuery);
    }

    public Map<ObjectId, Boolean> timezoneResponses() {
        Map<ObjectId, Boolean> timezones = new HashMap<>();
        for (Document o : userCollection.find()) {
            timezones.put((ObjectId) o.get("_id"), (Boolean) o.get("Print_Timezone"));
        }
        return timezones;
    }
}
