package Database;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface ServerSpecificAccessors {

    int getMusicVolume();

    Map<String, Boolean> getModules();

    List<Long> getRoles();

    Map<ObjectId, Boolean> timezoneResponses();

}
