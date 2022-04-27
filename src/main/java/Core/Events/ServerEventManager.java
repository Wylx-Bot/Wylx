package Core.Events;

import Core.Processing.MessageProcessing;
import Core.Wylx;
import Database.DbCollection;
import Database.DbManager;
import Database.ServerIdentifiers;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class ServerEventManager {

	// List of all process packages copied from message processing
	private static final EventPackage[] eventPackages = MessageProcessing.eventPackages;
	// Keep managers in cache so they don't have to be loaded from the db for every events (string is server id)
	private static final HashMap<String, ServerEventManager> cachedManagers = new HashMap<>();

	private static final DbManager db = Wylx.getInstance().getDb();

	// Static getting of events manager for a server
	public static ServerEventManager getServerEventManager(String id){
		// Try to find cached manager for the server
		ServerEventManager manager = cachedManagers.get(id);

		// If not cached load the manager from db and cache
		if(manager == null) {
			DbCollection<ServerIdentifiers> discordServer = db.getServerCollection();
			Document doc = discordServer.getSetting(id, ServerIdentifiers.Modules);
			manager = new ServerEventManager(doc);
			cachedManagers.put(id, manager);
		}

		return manager;
	}

	private static final String MODULE_MAP = "Modules";
	private static final String EXCEPTION_MAP = "Exceptions";

	// Map used for making comparisons as events are being run
	private final Map<String, Boolean> masterEventMap = new HashMap<>();
	// Map of enabled and disabled modules
	private final Map<String, Boolean> moduleMap = new HashMap<>();
	// Map of events that are exceptions to their modules
	private final Map<String, Boolean> eventExceptionMap = new HashMap<>();

	public ServerEventManager(Document document) {
		initValues(document);
	}

	public boolean checkEvent(Event event){
		return checkEvent(event.getClass().getSimpleName().toLowerCase());
	}

	public boolean checkEvent(String eventName){
		return masterEventMap.get(eventName);
	}

	public Boolean checkPackage(EventPackage eventPackage) {
		return checkPackage(eventPackage.getClass().getSimpleName().toLowerCase());
	}

	public Boolean checkPackage(String packageName){
		return moduleMap.get(packageName);
	}

	public void setModule(String moduleName, boolean value) throws IllegalArgumentException{
		// find the actual class for the module
		EventPackage module = null;
		for(EventPackage eventPackage : eventPackages){
			if(eventPackage.getClass().getSimpleName().toLowerCase().equals(moduleName))
				module = eventPackage;
		}

		// If the module doesnt exist we cant set anything with it
		if(module == null) throw new IllegalArgumentException("Specified module does not exist");

		// Write the value to the module map
		moduleMap.put(moduleName, value);

		for(Event event : module.getEvents()){
			String eventName = event.getClass().getSimpleName().toLowerCase();

			// Write changes to the map so the change to enabled status is reflected
			masterEventMap.put(eventName, value);

			// If there was an exception for this event remove that exception
			eventExceptionMap.remove(eventName);
		}
	}

	public void setEvent(String eventName, boolean value) throws IllegalArgumentException{
		// If the event doesn't exist we can set anything with it
		if(!masterEventMap.containsKey(eventName)) throw new IllegalArgumentException("Specified event: `" + eventName +  "` does not exist");

		// If the values are already the same do nothing
		Boolean currentValue = masterEventMap.get(eventName);
		if(currentValue != null && currentValue == value) return;

		// Write changes to the master map
		masterEventMap.replace(eventName, value);

		// If the exception already exists, an exception is no longer needed
		// Otherwise add an exception
		if(eventExceptionMap.containsKey(eventName)){
			eventExceptionMap.remove(eventName);
		} else {
			eventExceptionMap.put(eventName, value);
		}
	}

	private void initValues(Document document) {
		Map<String, Boolean> modules = document.get(MODULE_MAP, new HashMap<>());
		Map<String, Boolean> exceptions = document.get(EXCEPTION_MAP, new HashMap<>());

		// Make sure modules has every module
		for(EventPackage module : eventPackages){
			String moduleName = module.getClass().getSimpleName().toLowerCase();
			if (!modules.containsKey(moduleName)) {
				// Load the module that didn't exist
				modules.put(moduleName, true);
			}
		}

		// Intialize modules and exceptions
		modules.forEach(this::setModule);
		exceptions.forEach(this::setEvent);
	}

	public Document getDocument() {
		return new Document()
				.append(MODULE_MAP, moduleMap)
				.append(EXCEPTION_MAP, eventExceptionMap);
	}
}
