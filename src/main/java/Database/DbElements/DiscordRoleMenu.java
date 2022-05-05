package Database.DbElements;

import Database.DbElements.RoleMenuIdentifiers;
import Database.DiscordElement;
import com.mongodb.client.MongoClient;

import static com.mongodb.client.model.Filters.exists;

public class DiscordRoleMenu extends DiscordElement<RoleMenuIdentifiers> {
    private static final String MENUS_DOC = "Role_Menu";

    public DiscordRoleMenu(MongoClient client, String id) {
        super(client, id, MENUS_DOC, RoleMenuIdentifiers.values());
    }
}
