package Core.Events;

import Core.Processing.MessageProcessing;
import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.HashMap;
import java.util.Map;

public class ServerEventManager implements Codec<ServerEventManager> {

	// List of all process packages copied from message processing
	private static final EventPackage[] eventPackages = MessageProcessing.eventPackages;
	// Keep managers in cache so they don't have to be loaded from the db for every events (string is server id)
	private static final HashMap<String, ServerEventManager> cachedManagers = new HashMap<>();

	private static final DatabaseManager db = Wylx.getInstance().getDb();

	// Static getting of events manager for a server
	public static ServerEventManager getServerEventManager(String id){
		// Try to find cached manager for the server
		ServerEventManager manager = cachedManagers.get(id);

		// If not cached load the manager from db and cache
		if(manager == null) {
			DiscordServer discordServer = db.getServer(id);
			manager = discordServer.getSetting(ServerIdentifiers.Modules);
			manager.serverDB = discordServer;
			cachedManagers.put(id, manager);
		}

		return manager;
	}

	// DiscordServer this manager belongs to
	private DiscordServer serverDB;
	// Map used for making comparisons as events are being run
	private final HashMap<String, Boolean> masterEventMap = new HashMap<>();
	// Map of enabled and disabled modules
	private final HashMap<String, Boolean> moduleMap = new HashMap<>();
	// Map of events that are exceptions to their modules
	private final HashMap<String, Boolean> eventExceptionMap = new HashMap<>();

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

	private void setModule(String moduleName, boolean value, boolean write) throws IllegalArgumentException{
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

	private void setEvent(String eventName, boolean value, boolean write) throws IllegalArgumentException{
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
		for(EventPackage module : eventPackages){
			String moduleName = module.getClass().getSimpleName().toLowerCase();

			// Load the module that didn't exist
			setModule(moduleName, true, false);
		}
	}

	@Override
	public ServerEventManager decode(BsonReader reader, DecoderContext decoderContext) {
		// Read existing modules from the DB
		for(String moduleName = reader.readName(); !moduleName.equals("EXCEPTIONS"); moduleName = reader.readName()){
			setModule(moduleName, reader.readBoolean(), false);
		}

		// Add in any modules that didn't exist in the DB
		for(EventPackage module : eventPackages){
			String moduleName = module.getClass().getSimpleName().toLowerCase();
			// If the module is already loaded don't set it to default
			if(moduleMap.containsKey(moduleName)) continue;

			// Load the module that didn't exist
			setModule(moduleName, true, false);
		}

		boolean exceptions = reader.readBoolean();
		while(reader.readBsonType() != BsonType.END_OF_DOCUMENT){
			setEvent(reader.readName(), reader.readBoolean(), false);
		}

		return this;
	}

	@Override
	public void encode(BsonWriter writer, ServerEventManager value, EncoderContext encoderContext) {
		// Write the module
		for(Map.Entry<String, Boolean> entry : moduleMap.entrySet()){
			writer.writeBoolean(entry.getKey(), entry.getValue());
		}

		// Write if the server has event exceptions
		boolean exceptions = eventExceptionMap.size() != 0;
		writer.writeBoolean("EXCEPTIONS", exceptions);

		// Write the exceptions
		for(Map.Entry<String, Boolean> entry : eventExceptionMap.entrySet()){
			writer.writeBoolean(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Class<ServerEventManager> getEncoderClass() {
		return ServerEventManager.class;
	}
}