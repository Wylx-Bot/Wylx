package Database;

import java.util.Map;

public interface ServerSpecificAccessors {

    public int getMusicVolume();

    public Map<String, Boolean> getModules();

    public String[] getRoles();

    public boolean timezoneResponses(DiscordUser discordUser);

}
