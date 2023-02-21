package com.wylxbot.wylx.Core.Events;

import com.wylxbot.wylx.Core.Processing.MessageProcessing;
import com.wylxbot.wylx.Wylx;
import com.wylxbot.wylx.Database.DatabaseManager;

import java.util.HashMap;

public class ServerEventManager {

	// List of all process packages copied from message processing
	private static final EventPackage[] eventPackages = MessageProcessing.eventPackages;

	private static final DatabaseManager db = Wylx.getInstance().getDb();

	// Static getting of events manager for a server
	public static ServerEventManager getServerEventManager(String id){
		DiscordServer servedDB = db.getServer(id);
		// Try to find cached manager for the server
		ServerEventManager manager = servedDB.getSetting(ServerIdentifiers.Modules);
		manager.serverDB = servedDB;

		return manager;
	}

	// DiscordServer this manager belongs to
	private DiscordServer serverDB = null;
	// Map used for making comparisons as events are being run
	private final HashMap<String, Boolean> masterEventMap = new HashMap<>();
	// Map of enabled and disabled modules
	private final HashMap<String, Boolean> moduleMap = new HashMap<>();
	// Map of events that are exceptions to their modules
	private final HashMap<String, Boolean> eventExceptionMap = new HashMap<>();

	public ServerEventManager() {
		fillDefaults();
	}

	public boolean checkEvent(Event event){
		return checkEvent(event.getClass().getSimpleName().toLowerCase());
	}

	public boolean checkEvent(String eventName){
		if(masterEventMap.size() == 0) fillDefaults();
		return masterEventMap.get(eventName);
	}

	public Boolean checkPackage(EventPackage eventPackage) {
		return checkPackage(eventPackage.getClass().getSimpleName().toLowerCase());
	}

	public Boolean checkPackage(String packageName){
		return moduleMap.get(packageName);
	}

	public void setModule(String moduleName, boolean value) throws IllegalArgumentException{
		setModule(moduleName, value, true);
	}

	public void setModule(String moduleName, boolean value, boolean write) throws IllegalArgumentException{
		// find the actual class for the module
		EventPackage module = null;
		for(EventPackage eventPackage : eventPackages){
			if(eventPackage.getClass().getSimpleName().toLowerCase().equals(moduleName))
				module = eventPackage;
		}

		// If the module doesnt exist we cant set anything with it
		if(module == null) throw new IllegalArgumentException("Specified module does not exist");

		// if the values are already the same do nothing
		Boolean currentValue = moduleMap.get(moduleName);
		if(currentValue != null && currentValue == value) return;

		// Write the value to the module map
		moduleMap.put(moduleName, value);

		for(Event event : module.getEvents()){
			String eventName = event.getClass().getSimpleName().toLowerCase();

			// Write changes to the map so the change to enabled status is reflected
			masterEventMap.put(eventName, value);

			// If there was an exception for this event remove that exception
			eventExceptionMap.remove(eventName);
		}

		// Write the new info to mongodb
		if(write) serverDB.setSetting(ServerIdentifiers.Modules, this);
	}

	public void setEvent(String eventName, boolean value) throws IllegalArgumentException{
		setEvent(eventName, value, true);
	}

	public void setEvent(String eventName, boolean value, boolean write) throws IllegalArgumentException{
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

		// Write changes to mongodb
		if(write) serverDB.setSetting(ServerIdentifiers.Modules, this);
	}

	private void fillDefaults(){
		// Enable every module/command
		for(EventPackage module : eventPackages){
			String moduleName = module.getClass().getSimpleName().toLowerCase();
			setModule(moduleName, true, false);
		}
	}

	public HashMap<String, Boolean> getMasterEventMap() {
		return masterEventMap;
	}

	public HashMap<String, Boolean> getModuleMap() {
		return moduleMap;
	}

	public HashMap<String, Boolean> getEventExceptionMap() {
		return eventExceptionMap;
	}
}
