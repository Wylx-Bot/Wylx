package Database;

import org.apache.commons.codec.binary.Hex;

import java.util.TimeZone;

public class DiscordUser {
    String _id;
    TimeZone timeZone;

    public DiscordUser(String _id, TimeZone timeZone) {
        this._id = _id;
        this.timeZone = timeZone;
    }
}
