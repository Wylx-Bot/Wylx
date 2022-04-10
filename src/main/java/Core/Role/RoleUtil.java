package Core.Role;

import java.util.Arrays;
import java.util.List;

public class RoleUtil {

    public static List<String> commaArrayStripKeyword(String msg, String keyword) {
        String strippedStr = msg.substring(keyword.length() + 1);
        return Arrays.stream(strippedStr.split(",")).map(String::trim).toList();
    }
}
