package Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.exists;

public class DiscordServer implements ServerSpecificAccessors{
    String _id;
    private MongoDatabase mongoDatabase;
    private MongoCollection settingsCollection;
    private MongoCollection userCollection;

    public DiscordServer(MongoClient mongoClient, String _id) {
        this.mongoDatabase = mongoClient.getDatabase(_id);
        this._id = _id;
        settingsCollection = getSettings();
        userCollection = getUsers();
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
        String out = _id + "\n";
        for(String mongoCollection : mongoDatabase.listCollectionNames()) {
            out += ("\t" + mongoCollection + ": \n");
            for(Document document : mongoDatabase.getCollection(mongoCollection).find()) {
                out += ("\t\t" + document.toJson() + "\n");
            }
        }
        return out;
    }

    private MongoCollection getSettings() {
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
            init.add(new Document().append("Public_Roles", Arrays.asList())); // Role Settings
            settings.insertMany(init);
        }
        return settings;
    }

    private MongoCollection getUsers() {
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

    @Override
    public int getMusicVolume() {
        Document music_vol = (Document) settingsCollection.find(exists("Music_Volume")).first();
        assert music_vol != null;
        return music_vol.getInteger("Music_Volume");
    }

    public void setMusicVolume(int volume) {
        if(volume < 0 || volume > 100)
            throw new IllegalArgumentException("Volume must be [0,100]");
        settingsCollection.findOneAndUpdate(exists("Music_Volume"), //find the music volume setting
                new Document().append("Music_Volume", volume)); // update it
    }

    @Override
    public Map<String, Boolean> getModules() {
        Map<String, Boolean> modules = new HashMap<>();
        Document modulesEnabled = (Document) settingsCollection.find(exists("Modules_Enabled")).first();
        for (String key : modulesEnabled.keySet()) {
            if(key.equals("Modules_Enabled"))
                continue;
            modules.put(key, (Boolean) modulesEnabled.get(key));
        }
        return modules;
    }

    @Override
    public String[] getRoles() {
        Document modulesEnabled = (Document) settingsCollection.find(exists("Public_Roles")).first();
        return (String[]) modulesEnabled.get("Public_Roles");
    }

    @Override
    public Map<ObjectId, Boolean> timezoneResponses() {
        Map<ObjectId, Boolean> timezones = new HashMap<>();
        MongoCursor users = userCollection.find().iterator();
        while(users.hasNext()) {
            Document user = (Document) users.next();
            timezones.put((ObjectId) user.get("_id"), (Boolean) user.get("Print_Timezone"));
        }
        return timezones;
    }
}
