package Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// These identifiers are used in database access to ensure information is located correctly
public enum DocumentIdentifiers {
    DiscordUser("Discord_Tag", Database.DiscordUser.class, null),
    Modules("Modules_Enabled", Map.class, new HashMap<Long, Boolean>()),
    MusicVolume("Music_Volume", int.class, 100),
    Roles("Public_Roles", List.class, new ArrayList<Long>());

    public final String identifier;
    public final Class dataType;
    public final Object defaultValue;

    DocumentIdentifiers(String identifier, Class dataType, Object defaultValue) {
        this.identifier = identifier;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
    }
}
