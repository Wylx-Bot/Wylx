package Database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import java.util.*;

import static com.mongodb.MongoNamespace.checkDatabaseNameValidity;
import static com.mongodb.client.model.Filters.exists;

public class DiscordServer extends DiscordElement<ServerIdentifiers>{
    private static final String SERVER_SETTINGS_DOC = "Server_Settings";

    protected DiscordServer(MongoClient client, String id) {
        super(client, id, SERVER_SETTINGS_DOC, ServerIdentifiers.values());
    }
}
