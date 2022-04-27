package Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;

public class DbCollection<I extends DiscordIdentifiers> {
    private final MongoCollection<Document> collection;
    private final Map<String, Document> cache = new HashMap<>();

    DbCollection(MongoDatabase db, String name) {
        collection = db.getCollection(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSetting(String id, I identifiers) {
        Document doc = getDocument(id);
        if (doc.containsKey(identifiers.getIdentifier())) {
            return (T) doc.get(identifiers.getIdentifier());
        }

        return (T) identifiers.getDefaultValue();
    }

    public <T> void setSetting(String id, I identifiers, T obj) {
        // Update cache
        Document cachedDoc = getDocument(id);
        cachedDoc.put(identifiers.getIdentifier(), obj);


        Document insertData = newDocFromId(id);
        Document updateData = new Document().append(identifiers.getIdentifier(), obj);

        // Update operation
        Bson updateFilter = Filters.eq(id);
        Document upsert = new Document();
        upsert.put("$setOnInsert", insertData);
        upsert.put("$set", updateData);
        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

        collection.updateOne(updateFilter, upsert, updateOptions);
    }

    private Document newDocFromId(String id) {
        Document doc = new Document();
        doc.put("_id", id);
        return doc;
    }

    private Document getDocument(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        Bson filter = Filters.eq(id);
        Document doc = collection.find(filter).first();
        if (doc == null) {
            doc = newDocFromId(id);
        }

        cache.put(id, doc);
        return doc;
    }
}
