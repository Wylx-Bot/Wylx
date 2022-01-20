package Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.exists;

public class DiscordServer implements ServerSpecificAccessors{
    String _id;
    MongoDatabase mongoDatabase;
    MongoCollection mongoSettingsCollection = getSettings();

    public DiscordServer(MongoClient mongoClient, String _id) {
        this.mongoDatabase = mongoClient.getDatabase(_id);
        this._id = _id;
    }

    public String getAllServerInfo() {
        String out = "";
        for(String mongoCollection : mongoDatabase.listCollectionNames()) {
            out += ("\t" + mongoCollection + ": \n");
            for(Document document : mongoDatabase.getCollection(mongoCollection).find()) {
                out += ("\t\t" + document.toJson() + "\n");
            }
        }
        return out;
    }

    private MongoCollection getSettings() {
        return mongoDatabase.getCollection("Serverwide_Settings");
    }

    @Override
    public int getMusicVolume() {
        Document music_vol = (Document) mongoSettingsCollection.find(exists("Music_Volume")).first();
        assert music_vol != null;
        return music_vol.getInteger("Music_Volume");
    }

    @Override
    public Map<String, Boolean> getModules() {
        Map<String, Boolean> modules = new HashMap<>();
        Document modulesEnabled = (Document) mongoSettingsCollection.find(exists("Modules_Enabled")).first();
        for (String key : modulesEnabled.keySet()) {
            if(key.equals("Modules_Enabled"))
                continue;
            modules.put(key, (Boolean) modulesEnabled.get(key));
        }
        return modules;
    }

    @Override
    public String[] getRoles() {
        return new String[0];
    }

    @Override
    public boolean timezoneResponses(DiscordUser discordUser) {
        return false;
    }
}
