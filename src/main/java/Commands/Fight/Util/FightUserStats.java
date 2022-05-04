package Commands.Fight.Util;

import Core.Wylx;
import Database.DatabaseManager;
import Database.DbElements.DiscordUser;
import Database.DbElements.UserIdentifiers;
import net.dv8tion.jda.api.entities.Member;

public class FightUserStats {
    private final static int DEFAULT_HP = 500;
    public int hp = 500;
    public Member user;
    public DiscordUser userDb;


    // Stored in DB
    private int exp = 0;
    private int level = 1;

    private int hpLevel = 0;        // Level used for HP multiplier
    private int damageLevel = 0;    // Level used for Damage multiplier
    private int expMultLevel = 0;   // Level used for Experience multiplier
    private int speedLevel = 0;     // Level used for initial turn bias

    public FightUserStats() {}

    public FightUserStats(int exp, int level, int hpLevel, int damageLevel, int expMultLevel, int speedLevel){
        this.exp = exp;
        this.level = level;
        this.hpLevel = hpLevel;
        this.damageLevel = damageLevel;
        this.expMultLevel = expMultLevel;
        this.speedLevel = speedLevel;

        resetHP();
    }

    private static final DatabaseManager db = Wylx.getInstance().getDb();
    public static FightUserStats getUserStats(Member user) {
        DiscordUser dbUser = db.getUser(user.getId());
        FightUserStats stats = dbUser.getSetting(UserIdentifiers.FightStats);
        stats.user = user;
        stats.userDb = dbUser;
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

    public void resetHP(){
        hp = (int) (DEFAULT_HP * FightUtil.calcMultiplier(hpLevel));
    }

    public int getLvl() {
        return level;
    }

    // Returns level of a skill
    public int getStatLvl(FightStatTypes stat) {
        return switch (stat) {
            case HP -> this.hpLevel;
            case EXP -> this.expMultLevel;
            case SPEED -> this.speedLevel;
            case DAMAGE -> this.damageLevel;
        };
    }

    // Returns multiplier based off of the level of the skill
    public double getStatMultiplier(FightStatTypes stat) {
        return FightUtil.calcMultiplier(getStatLvl(stat));
    }

    public void save() {
        userDb.setSetting(UserIdentifiers.FightStats, this);
    }

    public int getExp() {
        return exp;
    }
}
