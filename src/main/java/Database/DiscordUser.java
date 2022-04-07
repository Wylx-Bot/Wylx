package Database;

import com.mongodb.client.MongoClient;

public class DiscordUser extends DiscordElement<UserIdentifiers> {
    private static final String SETTINGS_DOC = "User_Settings";
    protected DiscordUser(MongoClient client, String id) {
        super(client, id, SETTINGS_DOC, UserIdentifiers.values());
    }
}
