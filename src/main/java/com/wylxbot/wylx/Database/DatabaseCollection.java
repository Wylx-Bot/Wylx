package com.wylxbot.wylx.Database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.function.Function;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseCollection<T> {
    private final Function<String, T> defaultSupplier;
    private final MongoCollection<T> collection;

    public DatabaseCollection(Function<String, T> defaultSupplier, MongoDatabase db, String name, Class<T> clazz) {
        this.defaultSupplier = defaultSupplier;
        this.collection = db.getCollection(name, clazz);
    }

    public T getEntryOrNull(String id) {
        var iter = collection.find(eq("_id", id));
        return iter.first();
    }

    public T getEntryOrDefault(String id) {
        T entry = getEntryOrNull(id);
        if (entry == null) {
            entry = defaultSupplier.apply(id);
            collection.insertOne(entry);
        }

        return entry;
    }

    public void setEntry(String id, T entry) {
        collection.replaceOne(eq("_id", id), entry);
    }
}
