package Database.DbElements;

import Core.Events.ServerEventManager;
import Database.Codecs.ServerEventManagerCodec;
import Database.DiscordIdentifiers;
import org.bson.codecs.Codec;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

// These identifiers are used in database access to ensure information is located correctly
public enum ServerIdentifiers implements DiscordIdentifiers {
    Modules("Modules_Enabled", ServerEventManager.class, ServerEventManager::new, new ServerEventManagerCodec()),
    MusicVolume("Music_Volume", Integer.class, () -> 20),
    Prefix("Prefix", String.class, () -> ";"),
    PublicRoles("Public_Roles", List.class, ArrayList::new);

    public final String identifier;
    public final Class<?> dataType;
    public final Supplier<Object> defaultSupplier;
    public final Codec<?> codec;

    ServerIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue) {
        this(identifier, dataType, defaultValue, null);
    }

    ServerIdentifiers(String identifier, Class<?> dataType, Supplier<Object> defaultValue, Codec<?> codec) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultSupplier = defaultValue;
        this.codec = codec;
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
    public Codec<?> getCodec() {
        return codec;
    }

    @Override
    public Object getDefaultValue() {
        return defaultSupplier.get();
    }
}

