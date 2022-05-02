package Commands.Fight.Util;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class FightUserStatsCodec implements Codec<FightUserStats> {
    @Override
    public FightUserStats decode(BsonReader reader, DecoderContext decoderContext) {
        int exp = 0;
        int level = 0;
        int hpLevel = 0;
        int speedLevel = 0;
        int expMultLevel = 0;
        int damageLevel = 0;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            switch (name) {
                case "EXP" -> exp = reader.readInt32();
                case "Level" -> level = reader.readInt32();
                case "HP_Lvl" -> hpLevel = reader.readInt32();
                case "Speed_Lvl" -> speedLevel = reader.readInt32();
                case "EXP_Lvl" -> expMultLevel = reader.readInt32();
                case "Damage_Lvl" -> damageLevel = reader.readInt32();
            }
        }

        return new FightUserStats(exp, level, hpLevel, damageLevel, expMultLevel, speedLevel);
    }

    @Override
    public void encode(BsonWriter writer, FightUserStats value, EncoderContext encoderContext) {
        writer.writeInt32("EXP", value.getExp());
        writer.writeInt32("Level", value.getLevel());
        writer.writeInt32("HP_Lvl", value.getHpLevel());
        writer.writeInt32("Speed_Lvl", value.getSpeedLevel());
        writer.writeInt32("EXP_Lvl", value.getExpMultLevel());
        writer.writeInt32("Damage_Lvl", value.getDamageLevel());
    }

    @Override
    public Class<FightUserStats> getEncoderClass() {
        return FightUserStats.class;
    }
}
