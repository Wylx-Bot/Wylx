package Database;

import Core.Events.ServerEventManager;
import org.bson.Document;
import org.bson.codecs.Codec;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

// These identifiers are used in database access to ensure information is located correctly
public enum ServerIdentifiers implements DiscordIdentifiers{
    Modules("Modules_Enabled", ServerEventManager.class, Document::new),
    MusicVolume("Music_Volume", Integer.class, () -> 20),
    Prefix("Prefix", String.class, () -> ";"),
    PublicRoles("Public_Roles", List.class, ArrayList::new);

    public final String identifier;
    public final Class<?> dataType;
    public final Supplier<Object> defaultValue;

    ServerIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<?> getDataType() {
        return dataType;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue.get();
    }
}

