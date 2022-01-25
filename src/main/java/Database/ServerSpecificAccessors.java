package Database;

import org.bson.types.ObjectId;

import java.util.Map;

public interface ServerSpecificAccessors {

    public int getMusicVolume();

    public Map<String, Boolean> getModules();

    public String[] getRoles();

    public Map<ObjectId, Boolean> timezoneResponses();

}
