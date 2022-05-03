package Database.DbElements;

import Database.DiscordElement;
import com.mongodb.client.MongoClient;

public class DiscordUser extends DiscordElement<UserIdentifiers> {
    private static final String SETTINGS_DOC = "User_Settings";
    public DiscordUser(MongoClient client, String id) {
        super(client, id, SETTINGS_DOC, UserIdentifiers.values());
    }
}
