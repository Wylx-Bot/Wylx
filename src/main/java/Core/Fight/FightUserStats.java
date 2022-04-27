package Core.Fight;

import Core.Wylx;
import Database.DbCollection;
import Database.DbManager;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class FightUserStats extends Document {
    public int hp = 500;
    public Member user;

    private static final String EXP_KEY = "Exp";
    private static final String LEVEL_KEY = "Level";
    private static final String HP_KEY = "HP Level";
    private static final String DAMAGE_KEY = "Damage Level";
    private static final String EXP_MULT_KEY = "EXP Multiplier Level";
    private static final String SPEED_KEY = "Speed Level";

    public FightUserStats() {
        put(EXP_KEY, 0);
        put(LEVEL_KEY, 1);
        put(HP_KEY, 0);
        put(DAMAGE_KEY, 0);
        put(EXP_MULT_KEY, 0);
        put(SPEED_KEY, 0);
    }

    private static final DbManager db = Wylx.getInstance().getDb();
    public static FightUserStats getUserStats(Member user) {
        DbCollection<UserIdentifiers> dbUser = db.getUserCollection();
        FightUserStats stats = dbUser.getSetting(user.getId(), UserIdentifiers.FightStats);
        stats.user = user;
        return stats;
    }

    public boolean addExp(int addedExp) {
        boolean levelUp = false;
        int level = getLvl();
        int exp = getInteger(EXP_KEY) + addedExp;

        exp += getInteger(EXP_KEY);
        int expNextLvl = FightUtil.calcEXPForLevel(level);
        if (exp > expNextLvl) {
            exp -= expNextLvl;
            level++;
            levelUp = true;
        }

        this.put(EXP_KEY, exp);
        this.put(LEVEL_KEY, level);
        return levelUp;
    }

    public int getLvl() {
        return getInteger(LEVEL_KEY);
    }

    private String fightStatToKey(FightStatTypes stat) {
        return switch (stat) {
            case HP -> HP_KEY;
            case EXP -> EXP_MULT_KEY;
            case SPEED -> SPEED_KEY;
            case DAMAGE -> DAMAGE_KEY;
        };
    }

    // Returns level of a skill
    public int getStatLvl(FightStatTypes stat) {
        return getInteger(fightStatToKey(stat));
    }

    public void setStatLvl(FightStatTypes stat, int newLevel) {
        put(fightStatToKey(stat), newLevel);
    }

    // Returns multiplier based off of the level of the skill
    public double getStatMultiplier(FightStatTypes stat) {
        return FightUtil.calcMultiplier(getStatLvl(stat));
    }

    public void save() {
        DbCollection<UserIdentifiers> dbUser = db.getUserCollection();
        dbUser.setSetting(user.getId(), UserIdentifiers.FightStats, this);
    }
}
