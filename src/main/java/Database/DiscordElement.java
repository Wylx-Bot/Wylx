package Database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.Codec;

import static com.mongodb.MongoNamespace.checkDatabaseNameValidity;
import static com.mongodb.client.model.Filters.exists;

public abstract class DiscordElement<IdentifierType extends DiscordIdentifiers> {
    private final String id;

    // The database that holds this document
    private final MongoDatabase mongoDatabase;
    // Document holding the settings for this element
    private final MongoCollection<Document> settingsCollection;

    protected DiscordElement(MongoClient client, String id, String settingsDoc, IdentifierType[] identifiers){
        // ID of the document within the database
        this.id = id;

        try {
            checkDatabaseNameValidity(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal Database Name: " + id);
        }

        // Gets the database we want
        mongoDatabase = client.getDatabase(id);
        // Get the document we want from the DB
        settingsCollection = getSettingsCollection(settingsDoc).withCodecRegistry(MongoClientSettings.getDefaultCodecRegistry());

        // If there is nothing in our DB we need to give defaults
        if (settingsCollection.countDocuments() == 0) {
            initializeSettings(identifiers);
        }
    }

    /**
     * Returns a string representation of the Server.
     * The Format is
     * <blockquote>
     *       <pre>
     * * _id
     * *   CollectionName:
     * *       DocumentJSON
     * *       DocumentJSON
     * *   ...
     * * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(id + "\n");
        for(String mongoCollection : mongoDatabase.listCollectionNames()) {
            out.append("\t").append(mongoCollection).append(": \n");
            for(Document document : mongoDatabase.getCollection(mongoCollection).find()) {
                out.append("\t\t").append(document.toJson()).append("\n");
            }
        }
        return out.toString();
    }

    private MongoCollection<Document> getSettingsCollection(String documentName) {
        // Go through all the collections within the database in order to find our collection
        for (String name : mongoDatabase.listCollectionNames()) {
            if (name.equals(documentName)) {
                return mongoDatabase.getCollection(documentName);
            }
        }
        mongoDatabase.createCollection(documentName);
        return mongoDatabase.getCollection(documentName);
    }

    private void initializeSettings(IdentifierType[] identifiers){
        for(IdentifierType i : identifiers){
            setSetting(i, i.getDefaultValue());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getSettingOrNull(IdentifierType identifier) {
        // Get the document relevant to our setting
        Document settingDoc = settingsCollection.find(exists(identifier.getIdentifier())).first();

        if (settingDoc == null) {
            return null;
        }

        // Non-complex can be decoded with default codecs
        if (!settingDoc.get("complex", false)) {
            return settingDoc.get(identifier.getIdentifier(), (T) identifier.getDefaultValue());
        }

        // For complex objects we first need to turn our setting into a bson doc to be decoded
        BsonReader reader = settingDoc.get(identifier.getIdentifier(), new Document()).toBsonDocument().asBsonReader();
        // Remove the start tag from the beginning
        reader.readStartDocument();
        // Hand off to the codec to finish decoding
        return ((Codec<T>) identifier.getDefaultValue()).decode(reader, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(IdentifierType identifier){
        T value = getSettingOrNull(identifier);

        // If the settings doc is null we need to return the default value
        if(value == null) {
            return (T) identifier.getDefaultValue();
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> void setSetting(IdentifierType identifier, Object data){
        // Ensure that the identifier and data are matched
        if(identifier.getDataType().cast(data) == null)
            throw new IllegalArgumentException("Identifier data type mismatch");

        // Get the place for the setting
        Document settingDoc = settingsCollection.find(exists(identifier.getIdentifier())).first();
        // If the setting exists already remove it so it can be replaced
        if(settingDoc != null)
            settingsCollection.deleteOne(exists(identifier.getIdentifier()));

        // If the data is *fancy* do *fancy* things with it
        if(data instanceof Codec<?>){
            // Create a document and writer to put the data in
            BsonDocument complexDocument = new BsonDocument();
            BsonWriter complexWriter = new BsonDocumentWriter(complexDocument);

            // Write data from our object into the document
            complexWriter.writeStartDocument();
            ((Codec<T>) data).encode(complexWriter, (T) data, null);
            complexWriter.writeEndDocument();

            // Set data to document so it can be written to the DB
            data = complexDocument;
        }

        // Put data into document
        settingDoc = new Document().append(identifier.getIdentifier(), data);
        // Record if the data is complex
        settingDoc.put("complex", data instanceof BsonDocument);
        // Insert data into db
        settingsCollection.insertOne(settingDoc);
    }
}
