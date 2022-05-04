package Database.Codecs;

import Core.Events.EventPackage;
import Core.Events.ServerEventManager;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Map;

import static Core.Processing.MessageProcessing.eventPackages;

public class ServerEventManagerCodec implements Codec<ServerEventManager> {
    @Override
    public ServerEventManager decode(BsonReader reader, DecoderContext decoderContext) {
        ServerEventManager eventManager = new ServerEventManager();

        // Read existing modules from the DB
        for(String moduleName = reader.readName(); !moduleName.equals("EXCEPTIONS"); moduleName = reader.readName()){
            eventManager.setModule(moduleName, reader.readBoolean(), false);
        }

        // Add in any modules that didn't exist in the DB
        for(EventPackage module : eventPackages){
            String moduleName = module.getClass().getSimpleName().toLowerCase();
            // If the module is already loaded don't set it to default
            if(eventManager.getModuleMap().containsKey(moduleName)) continue;

            // Load the module that didn't exist
            eventManager.setModule(moduleName, true, false);
        }

        boolean exceptions = reader.readBoolean();
        while(reader.readBsonType() != BsonType.END_OF_DOCUMENT){
            eventManager.setEvent(reader.readName(), reader.readBoolean(), false);
        }

        return eventManager;
    }

    @Override
    public void encode(BsonWriter writer, ServerEventManager value, EncoderContext encoderContext) {
        // Write the module
        for(Map.Entry<String, Boolean> entry : value.getModuleMap().entrySet()){
            writer.writeBoolean(entry.getKey(), entry.getValue());
        }

        // Write if the server has event exceptions
        boolean exceptions = value.getEventExceptionMap().size() != 0;
        writer.writeBoolean("EXCEPTIONS", exceptions);

        // Write the exceptions
        for(Map.Entry<String, Boolean> entry : value.getEventExceptionMap().entrySet()){
            writer.writeBoolean(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Class<ServerEventManager> getEncoderClass() {
        return ServerEventManager.class;
    }
}
