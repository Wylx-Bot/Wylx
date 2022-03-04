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
        initializeSettings();
        userCollection = getUsersCollection();
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
    
//    private MongoCollection<Document> getSettingsCollection() {
//        MongoCollection<Document> settings = mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
//        if(settings.estimatedDocumentCount() == 0) { // if this is a new database (the bot server settings need to be initialised)
//            mongoDatabase.createCollection(SERVER_SETTINGS_DOC);
//            settings = mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
//            ArrayList<Document> init = new ArrayList<>();
//            init.add(new Document().append("Music_Volume", 20)); // Music Setting Document
//            init.add(new Document().append("Dice Rolling", true) // Modules Setting Document
//                                   .append("Music", true)
//                                   .append("Roles", true)
//                                   .append("Timezones", true)
//                                   .append("Modules_Enabled", 4));
//            init.add(new Document().append(DocumentIdentifiers.Roles.identifier, Collections.emptyList())); // Role Settings
//            settings.insertMany(init);
//        }
//        return settings;
//    }

    private MongoCollection<Document> getSettingsCollection() {
        MongoCollection<Document> settings = mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
        if(settings.estimatedDocumentCount() == 0) { // if this is a new database (the bot server settings need to be initialised)
            mongoDatabase.createCollection(SERVER_SETTINGS_DOC);
            settings = mongoDatabase.getCollection(SERVER_SETTINGS_DOC);
        }
        return settings;
    }

    public void initializeSettings() {
        for(DocumentIdentifiers d : DocumentIdentifiers.values()) {
            setSetting(d, d.defaultValue);
        }
    }

    /** Returns the setting value in the first doc with a setting of that name or null if no such setting exists
     * @param identifier the preset identifier for a server setting
     * @return An object of whatever type is stored by MongoDB
     */
    public Object getSetting(DocumentIdentifiers identifier) {
        Document settingDoc = settingsCollection.find(exists(identifier.identifier)).first();
        if(settingDoc == null)
            return null;
        return settingDoc.get(identifier.identifier);
    }

    public void setSetting(DocumentIdentifiers identifier, Object data) {
        if(data.getClass() != identifier.dataType)
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

    public void removeSettingDocument(DocumentIdentifiers identifier) {
        Document settingDoc = settingsCollection.find(exists(identifier.identifier)).first();
        if(settingDoc == null)
            return;
        settingsCollection.deleteOne(exists(identifier.identifier));
    }

    private MongoCollection<Document> getUsersCollection() {
        MongoCollection<Document> users = mongoDatabase.getCollection(USER_SETTINGS_DOC);
        if(users.estimatedDocumentCount() == 0) { // if this is a new database (the bot server settings need to be initialised)
            mongoDatabase.createCollection(USER_SETTINGS_DOC);
            users = mongoDatabase.getCollection(USER_SETTINGS_DOC);
            ArrayList<Document> init = new ArrayList<>();
            // I don't think we have to initialise any users but we can here if we want
            users.insertMany(init);
        }
        return users;
    }

    public Map<String, Map<String, String>> getUsers() {
        Map<String, Map<String, String>> users = new HashMap<>();
        for(Document user : userCollection.find()) {
            Map<String, String> data = new HashMap<>();
            for(String k : user.keySet()) {
                data.put(k, user.get(k).toString());
            }
            users.put(data.get(DocumentIdentifiers.DiscordUser.identifier), data);
        }
        return users;
    }

    public void addUser(String discordTag, Map<String, String> data) {
        if(getUser(discordTag) != null)
            removeUser(discordTag);
        Document user = new Document();
        user.append(DocumentIdentifiers.DiscordUser.identifier, discordTag);
        for(String k : data.keySet()) {
            user.append(k, data.get(k));
        }
        userCollection.insertOne(user);
    }

    public Map<String, String> getUser(String discordTag) {
        Map<String, String> data = new HashMap<>();
        BasicDBObject matchQuery = new BasicDBObject();
        matchQuery.put(DocumentIdentifiers.DiscordUser.identifier, discordTag);
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

//    public int getMusicVolume() {
//        Document music_vol = settingsCollection.find(exists(DocumentIdentifiers.MusicVolume.identifier)).first();
//        assert music_vol != null;
//        return music_vol.getInteger(DocumentIdentifiers.MusicVolume.identifier);
//    }
//
//    public void setMusicVolume(int volume) {
//        if(volume < 0 || volume > 100)
//            throw new IllegalArgumentException("Volume must be [0,100]");
//        settingsCollection.findOneAndReplace(exists(DocumentIdentifiers.MusicVolume.identifier), //find the music volume setting
//                new Document().append(DocumentIdentifiers.MusicVolume.identifier, volume)); // update it
//    }
//
//    public Map<String, Boolean> getModules() {
//        Map<String, Boolean> modules = new HashMap<>();
//        Document modulesEnabled = settingsCollection.find(exists(DocumentIdentifiers.Modules.identifier)).first();
//        assert modulesEnabled != null;
//        for (String key : modulesEnabled.keySet()) {
//            if(key.equals(DocumentIdentifiers.Modules.identifier) || key.equals("_id"))
//                continue;
//            modules.put(key, (Boolean) modulesEnabled.get(key));
//        }
//        return modules;
//    }
//
//    public void setModule(String moduleName, boolean state) {
//        Document modulesEnabled = settingsCollection.find(exists(DocumentIdentifiers.Modules.identifier)).first();
//        assert modulesEnabled != null;
//        Object numEnabled = modulesEnabled.get(moduleName);
//        if(numEnabled == null) // if moduleName is new add it
//            modulesEnabled.put(DocumentIdentifiers.Modules.identifier, (int) modulesEnabled.get(DocumentIdentifiers.Modules.identifier) + 1);
//        else if(!numEnabled.equals(state))
//            if(state) // if the module is being enabled
//                modulesEnabled.put(DocumentIdentifiers.Modules.identifier, (int) modulesEnabled.get(DocumentIdentifiers.Modules.identifier) + 1);
//            else // if the module is being disabled
//                modulesEnabled.put(DocumentIdentifiers.Modules.identifier, (int) modulesEnabled.get(DocumentIdentifiers.Modules.identifier) - 1);
//        modulesEnabled.put(moduleName, state);
//        settingsCollection.findOneAndReplace(exists(DocumentIdentifiers.Modules.identifier), modulesEnabled);
//    }
//
//    public List<Long> getRoles() {
//        Document publicRoles = settingsCollection.find(exists(DocumentIdentifiers.Roles.identifier)).first();
//        if(publicRoles == null)
//            return null;
//        return publicRoles.getList(DocumentIdentifiers.Roles.identifier, Long.class);
//    }
//
//    // array list of roles given a category name, returns null if no roles exist
//    public List<Long> getRoles(String category) {
//        Document roles = settingsCollection.find(exists(category)).first();
//        if(roles == null)
//            return null;
//        return roles.getList(category, Long.class);
//    }
//
//    public List<Long> addRole(String category, String role) {
//        Document roles = settingsCollection.find(exists(category)).first();
//        if(roles == null) {
//            roles = (settingsCollection.find(exists(category)).first()).append(category, List.of(role));
//        } else {
//            ArrayList<String> roleList = (ArrayList<String>) roles.get(category);
//            if(!roleList.contains(role))
//                roleList.add(role);
//            roles.put(category, roleList);
//        }
//        settingsCollection.findOneAndReplace(exists(category), roles);
//        return getRoles(category);
//    }
//
//    public List<Long> addRole(String role) {
//        Document roles = settingsCollection.find(exists(DocumentIdentifiers.Roles.identifier)).first();
//        ArrayList<String> roleList = (ArrayList<String>) roles.get(DocumentIdentifiers.Roles.identifier);
//        if(!roleList.contains(role))
//            roleList.add(role);
//        roles.put(DocumentIdentifiers.Roles.identifier, roleList);
//        settingsCollection.findOneAndReplace(exists(DocumentIdentifiers.Roles.identifier), roles);
//        return getRoles();
//    }
//
//    public List<Long> removeRoll(String role) {
//        Document roles = settingsCollection.find(exists(DocumentIdentifiers.Roles.identifier)).first();
//        if(roles == null)
//            return null;
//        ArrayList<String> roleList = (ArrayList<String>) roles.get(DocumentIdentifiers.Roles.identifier);
//        roleList.remove(role);
//        roles.put(DocumentIdentifiers.Roles.identifier, roleList);
//        settingsCollection.findOneAndReplace(exists(DocumentIdentifiers.Roles.identifier), roles);
//        return getRoles();
//    }
//
//    public List<Long> removeRole(String category, String role) {
//        Document roles = settingsCollection.find(exists(category)).first();
//        if(roles == null) {
//            return null;
//        } else {
//            ArrayList<String> roleList = (ArrayList<String>) roles.get(category);
//            roleList.remove(role);
//            roles.put(category, roleList);
//        }
//        settingsCollection.findOneAndReplace(exists(category), roles);
//        return getRoles(category);
//    }
//
    public Map<ObjectId, Boolean> timezoneResponses() {
        Map<ObjectId, Boolean> timezones = new HashMap<>();
        for (Document o : userCollection.find()) {
            timezones.put((ObjectId) o.get("_id"), (Boolean) o.get("Print_Timezone"));
        }
        return timezones;
    }
}
