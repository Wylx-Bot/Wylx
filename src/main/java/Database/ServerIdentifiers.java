package Database;

import Core.Events.ServerEventManager;

import java.util.*;

// These identifiers are used in database access to ensure information is located correctly
public enum ServerIdentifiers {
    Modules("Modules_Enabled", ServerEventManager.class, new ServerEventManager()),
    MusicVolume("Music_Volume", Integer.class, 20),
    Roles("Public_Roles", List.class, Collections.emptyList()),
    Prefix("Prefix", String.class, ";");

    public final String identifier;
    public final Class dataType;
    public final Object defaultValue;

    ServerIdentifiers(String identifier, Class dataType, Object defaultValue) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }
}

