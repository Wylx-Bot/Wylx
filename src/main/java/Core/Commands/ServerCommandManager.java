package Core.Commands;

import Core.Processing.MessageProcessing;
import Core.Processing.ProcessPackage;
import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import com.mongodb.internal.connection.Server;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ServerCommandManager implements Codec<ServerCommandManager> {

	// List of all process packages copied from message processing
	private static final ProcessPackage[] processPackages = MessageProcessing.processPackages;
	// Keep managers in cache so they don't have to be loaded from the db for every command (string is server id)
	private static final HashMap<String, ServerCommandManager> cachedManagers = new HashMap<>();

	private static final DatabaseManager db = Wylx.getInstance().getDb();

	// Static getting of command manager for a server
	public static ServerCommandManager getServerCommandManager(String id){
		// Try to find cached manager for the server
		ServerCommandManager manager = cachedManagers.get(id);

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
	// Map used for making comparisons as commands are being run
	private final HashMap<String, Boolean> masterCommandMap = new HashMap<>();
	// Map of enabled and disabled modules
	private final HashMap<String, Boolean> moduleMap = new HashMap<>();
	// Map of commands that are exceptions to their modules
	private final HashMap<String, Boolean> commandExceptionMap = new HashMap<>();

	public boolean checkCommand(String commandName){
		return masterCommandMap.get(commandName);
	}

	public void setModule(String moduleName, boolean value){
		setModule(moduleName, value, true);
	}

	private void setModule(String moduleName, boolean value, boolean write){
		// find the actual class for the module
		ProcessPackage module = null;
		for(ProcessPackage processPackage : processPackages){
			if(processPackage.getClass().getSimpleName().equals(moduleName))
				module = processPackage;
		}

		if(module == null) throw new IllegalArgumentException("Specified module does not exist");

		// if the values are already the same do nothing
		if(moduleMap.get(moduleName) == value) return;

		// Write the value to the module map
		moduleMap.replace(moduleName, value);

		for(ServerCommand command : module.getCommands()){
			String commandName = command.getClass().getSimpleName();

			// Write changes to the map so the change to enabled status is reflected
			masterCommandMap.replace(commandName, value);

			// If there was an exception for this command remove that exception
			commandExceptionMap.remove(commandName);
		}

		// Write the new info to mongodb
		if(write) serverDB.setSetting(ServerIdentifiers.Modules, this);
	}

	@Override
	public ServerCommandManager decode(BsonReader reader, DecoderContext decoderContext) {
		return null;
	}

	@Override
	public void encode(BsonWriter writer, ServerCommandManager value, EncoderContext encoderContext) {

	}

	@Override
	public Class<ServerCommandManager> getEncoderClass() {
		return null;
	}
}
