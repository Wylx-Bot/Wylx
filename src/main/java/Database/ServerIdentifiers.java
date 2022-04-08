package Database;

import Core.Events.ServerEventManager;
import org.bson.codecs.Codec;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

// These identifiers are used in database access to ensure information is located correctly
public enum ServerIdentifiers implements DiscordIdentifiers{
    Modules("Modules_Enabled", ServerEventManager.class, new ServerEventManager()),
    MusicVolume("Music_Volume", Integer.class, 20),
    Prefix("Prefix", String.class, ";");

    public final String identifier;
    public final Class<?> dataType;
    public final Object defaultValue;

    ServerIdentifiers(String identifier, Class dataType, Object defaultValue) {
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
        if(!(defaultValue instanceof Codec<?>))
            return defaultValue;
        else {
            try {
                return dataType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        }
    }
}

