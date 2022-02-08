package Database;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Map;

public interface ServerSpecificAccessors {

    int getMusicVolume();

    Map<String, Boolean> getModules();

    ArrayList<String> getRoles();

    Map<ObjectId, Boolean> timezoneResponses();

}
