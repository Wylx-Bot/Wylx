package Core.Roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleUtil {
    public static List<String> commaArrayStripKeyword(String msg) {
        String strippedStr = msg.substring(msg.indexOf(' ')).strip();
        return Arrays.stream(strippedStr.split(",")).map(String::trim).toList();
    }

    public static List<Role> getRolesFromStrings(List<String> roles, Guild guild, List<String> invalid) {
        List<Role> list = new ArrayList<>();
        for (String roleStr : roles) {
            try {
                long id = Long.parseLong(roleStr);
                Role roleObj = guild.getRoleById(id);
                if (roleObj != null) {
                    list.add(roleObj);
                    continue;
                }
            } catch (NumberFormatException e) {
                // May be
            }

            // Try search for the role by name
            List<Role> roleObjs = guild.getRolesByName(roleStr, true);
            if (roleObjs.size() != 0) {
                list.addAll(roleObjs);
            } else if (invalid != null) {
                invalid.add(roleStr);
            }
        }

        return list;
    }

    public static List<Role> getRolesFromIds(List<Long> roles, Guild guild) {
        List<Role> list = new ArrayList<>();
        for (long id : roles) {
            Role newRole = guild.getRoleById(id);
            if (newRole != null) {
                list.add(newRole);
            } else {
                // TODO: Non existant role??
            }
        }

        return list;
    }
}
