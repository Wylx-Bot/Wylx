package Database;

import java.util.List;
import java.util.Map;

// These identifiers are used in database access to ensure information is located correctly
public enum DocumentIdentifiers {
    DiscordUser("Discord_Tag", Database.DiscordUser.class),
    Modules("Modules_Enabled", Map.class),
    MusicVolume("Music_Volume", int.class),
    Roles("Public_Roles", List.class);

    public final String identifier;
    public final Class dataType;

    DocumentIdentifiers(String identifier, Class dataType) {
        this.identifier = identifier;
        this.dataType = dataType;
    }
}
