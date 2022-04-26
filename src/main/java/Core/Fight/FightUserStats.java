package Core.Fight;

import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordUser;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class FightUserStats implements Codec<FightUserStats> {
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

    public int getLvl() {
        return level;
    }

    public int getExp() {
        return exp;
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

    public int getUsedPoints() {
        return this.hpLevel + this.expMultLevel + this.speedLevel + this.damageLevel;
    }

    // Returns multiplier based off of the level of the skill
    public double getStatMultiplier(FightStatTypes stat) {
        return FightUtil.calcMultiplier(getStatLvl(stat));
    }

    public void save() {
        userDb.setSetting(UserIdentifiers.FightStats, this);
    }

    @Override
    public FightUserStats decode(BsonReader reader, DecoderContext decoderContext) {
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            switch (name) {
                case "EXP" -> this.exp = reader.readInt32();
                case "Level" -> this.level = reader.readInt32();
                case "HP_Lvl" -> this.hpLevel = reader.readInt32();
                case "Speed_Lvl" -> this.speedLevel = reader.readInt32();
                case "EXP_Lvl" -> this.expMultLevel = reader.readInt32();
                case "Damage_Lvl" -> this.damageLevel = reader.readInt32();
            }
        }

        this.hp *= FightUtil.calcMultiplier(this.hpLevel);
        return this;
    }

    @Override
    public void encode(BsonWriter writer, FightUserStats value, EncoderContext encoderContext) {
        writer.writeInt32("EXP", this.exp);
        writer.writeInt32("Level", this.level);
        writer.writeInt32("HP_Lvl", this.hpLevel);
        writer.writeInt32("Speed_Lvl", this.speedLevel);
        writer.writeInt32("EXP_Lvl", this.expMultLevel);
        writer.writeInt32("Damage_Lvl", this.damageLevel);
    }

    @Override
    public Class<FightUserStats> getEncoderClass() {
        return FightUserStats.class;
    }
}
