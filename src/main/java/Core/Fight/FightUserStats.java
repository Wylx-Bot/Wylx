package Core.Fight;

import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordUser;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class FightUserStats implements Codec<FightUserStats> {
    public int hp = 500;
    public Member user;
    private int exp = 0;
    private int level = 1;

    public FightUserStats() {}

    private static final DatabaseManager db = Wylx.getInstance().getDb();
    public static FightUserStats getUserStats(Member user) {
        DiscordUser dbUser = db.getUser(user.getId());
        FightUserStats stats = dbUser.getSetting(UserIdentifiers.FightStats);
        stats.user = user;
        return stats;
    }

    public boolean addExp(int exp) {
        this.exp += exp;
        int expNextLvl = FightUtil.calcEXPForLevel(level);
        if (this.exp > expNextLvl) {
            this.exp -= expNextLvl;
            this.level++;
            return true;
        }

        return false;
    }

    public int getLvl() {
        return level;
    }

    @Override
    public FightUserStats decode(BsonReader reader, DecoderContext decoderContext) {
        return null;
    }

    @Override
    public void encode(BsonWriter writer, FightUserStats value, EncoderContext encoderContext) {

    }

    @Override
    public Class<FightUserStats> getEncoderClass() {
        return null;
    }
}
