package Database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.*;

import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.exists;

public class DiscordServer implements ServerSpecificAccessors{
    String _id;
    private MongoDatabase mongoDatabase;
    private MongoCollection settingsCollection;
    private MongoCollection userCollection;

    public DiscordServer(MongoClient mongoClient, String databaseName) {
        this.mongoDatabase = mongoClient.getDatabase(databaseName);
        this._id = databaseName;
        settingsCollection = getSettingsCollection();
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

    private MongoCollection getSettingsCollection() {
        MongoCollection settings = mongoDatabase.getCollection("Serverwide_Settings");
        if(settings == null) { // if this is a new database (the bot server settings need to be initialised)
            mongoDatabase.createCollection("Serverwide_Settings");
            settings = mongoDatabase.getCollection("Serverwide_Settings");
            ArrayList<Document> init = new ArrayList<>();
            init.add(new Document().append("Music_Volume", 100)); // Music Setting Document
            init.add(new Document().append("Dice Rolling", true) // Modules Setting Document
                                   .append("Music", true)
                                   .append("Roles", true)
                                   .append("Timezones", true)
                                   .append("Modules_Enabled", 4));
            init.add(new Document().append("Public_Roles", Collections.emptyList())); // Role Settings
            settings.insertMany(init);
        }
        return settings;
    }

    public void initSettings() {
        mongoDatabase.createCollection("Serverwide_Settings");
        MongoCollection settings = mongoDatabase.getCollection("Serverwide_Settings");
        ArrayList<Document> init = new ArrayList<>();
        init.add(new Document().append("Music_Volume", 100)); // Music Setting Document
        init.add(new Document().append("Dice Rolling", true) // Modules Setting Document
                .append("Music", true)
                .append("Roles", true)
                .append("Timezones", true)
                .append("Modules_Enabled", 4));
        init.add(new Document().append("Public_Roles", Arrays.asList())); // Role Settings
        settings.insertMany(init);
        this.settingsCollection = settings;
    }

    private MongoCollection getUsersCollection() {
        MongoCollection users = mongoDatabase.getCollection("User_Settings");
        if(users == null) { // if this is a new database (the bot server settings need to be initialised)
            mongoDatabase.createCollection("User_Settings");
            users = mongoDatabase.getCollection("User_Settings");
            ArrayList<Document> init = new ArrayList<>();
            // I don't think we have to initialise any users but we can here if we want
            users.insertMany(init);
        }
        return users;
    }

    public Map<String, Map<String, String>> getUsers() {
        Map<String, Map<String, String>> users = new HashMap<>();
        for(Object user : userCollection.find()) {
            Map<String, String> data = new HashMap<>();
            Document u = (Document) user;
            for(String k : u.keySet()) {
                data.put(k, u.get(k).toString());
            }
            users.put(data.get("Discord_Tag"), data);
        }
        return users;
    }

    public void addUser(String discordTag, Map<String, String> data) {
        if(getUser(discordTag) != null)
            removeUser(discordTag);
        Document user = new Document();
        user.append("Discord_Tag", discordTag);
        for(String k : data.keySet()) {
            user.append(k, data.get(k));
        }
        userCollection.insertOne(user);
    }

    public Map<String, String> getUser(String discordTag) {
        Map<String, String> data = new HashMap<>();
        BasicDBObject matchQuery = new BasicDBObject();
        matchQuery.put("Discord_Tag", discordTag);
        Document user = (Document)  userCollection.find(matchQuery).first();
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

    @Override
    public int getMusicVolume() {
        Document music_vol = (Document) settingsCollection.find(exists("Music_Volume")).first();
        assert music_vol != null;
        return music_vol.getInteger("Music_Volume");
    }

    public void setMusicVolume(int volume) {
        if(volume < 0 || volume > 100)
            throw new IllegalArgumentException("Volume must be [0,100]");
        settingsCollection.findOneAndReplace(exists("Music_Volume"), //find the music volume setting
                new Document().append("Music_Volume", volume)); // update it
    }

    @Override
    public Map<String, Boolean> getModules() {
        Map<String, Boolean> modules = new HashMap<>();
        //System.out.println("SETTING FOUND: " + settingsCollection.find(exists("Modules_Enabled")).first());
        Document modulesEnabled = (Document) settingsCollection.find(exists("Modules_Enabled")).first();
        for (String key : modulesEnabled.keySet()) {
            if(key.equals("Modules_Enabled") || key.equals("_id"))
                continue;
            modules.put(key, (Boolean) modulesEnabled.get(key));
        }
        return modules;
    }

    public void setModule(String moduleName, boolean state) {
        Document modulesEnabled = (Document) settingsCollection.find(exists("Modules_Enabled")).first();
        Object numEnabled = modulesEnabled.get(moduleName);
        if(numEnabled == null) // if moduleName is new add it
            modulesEnabled.put("Modules_Enabled", (int) modulesEnabled.get("Modules_Enabled") + 1);
        else if(!numEnabled.equals(state))
            if(state) // if the module is being enabled
                modulesEnabled.put("Modules_Enabled", (int) modulesEnabled.get("Modules_Enabled") + 1);
            else // if the module is being disabled
                modulesEnabled.put("Modules_Enabled", (int) modulesEnabled.get("Modules_Enabled") - 1);
        modulesEnabled.put(moduleName, state);
        settingsCollection.findOneAndReplace(exists("Modules_Enabled"), modulesEnabled);
    }

    // array list of roles in public roles
    @Override
    public ArrayList<String> getRoles() {
        Document publicRoles = (Document) settingsCollection.find(exists("Public_Roles")).first();
        if(publicRoles == null)
            return null;
        return (ArrayList<String>) publicRoles.get("Public_Roles");
    }

    // array list of roles given a category name, returns null if no roles exist
    public ArrayList<String> getRoles(String category) {
        Document roles = (Document) settingsCollection.find(exists(category)).first();
        if(roles == null)
            return null;
        return (ArrayList<String>) roles.get(category);
    }

    public ArrayList<String> addRole(String category, String role) {
        Document roles = (Document) settingsCollection.find(exists(category)).first();
        if(roles == null) {
            roles = ((Document) settingsCollection.find(exists(category)).first()).append(category, Arrays.asList().add(role));
        } else {
            ArrayList<String> roleList = (ArrayList<String>) roles.get(category);
            if(!roleList.contains(role))
                roleList.add(role);
            roles.put(category, roleList);
        }
        settingsCollection.findOneAndReplace(exists(category), roles);
        return getRoles(category);
    }

    public ArrayList<String> addRole(String role) {
        Document roles = (Document) settingsCollection.find(exists("Public_Roles")).first();
        ArrayList<String> roleList = (ArrayList<String>) roles.get("Public_Roles");
        if(!roleList.contains(role))
            roleList.add(role);
        roles.put("Public_Roles", roleList);
        settingsCollection.findOneAndReplace(exists("Public_Roles"), roles);
        return getRoles();
    }

    public ArrayList<String> removeRoll(String role) {
        Document roles = (Document) settingsCollection.find(exists("Public_Roles")).first();
        if(roles == null)
            return null;
        ArrayList<String> roleList = (ArrayList<String>) roles.get("Public_Roles");
        roleList.remove(role);
        roles.put("Public_Roles", roleList);
        settingsCollection.findOneAndReplace(exists("Public_Roles"), roles);
        return getRoles();
    }

    public ArrayList<String> removeRole(String category, String role) {
        Document roles = (Document) settingsCollection.find(exists(category)).first();
        if(roles == null) {
            return null;
        } else {
            ArrayList<String> roleList = (ArrayList<String>) roles.get(category);
            roleList.remove(role);
            roles.put(category, roleList);
        }
        settingsCollection.findOneAndReplace(exists(category), roles);
        return getRoles(category);
    }

    @Override
    public Map<ObjectId, Boolean> timezoneResponses() {
        Map<ObjectId, Boolean> timezones = new HashMap<>();
        for (Object o : userCollection.find()) {
            Document user = (Document) o;
            timezones.put((ObjectId) user.get("_id"), (Boolean) user.get("Print_Timezone"));
        }
        return timezones;
    }
}
