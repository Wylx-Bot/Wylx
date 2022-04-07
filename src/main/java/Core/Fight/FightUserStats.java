package Core.Fight;

import net.dv8tion.jda.api.entities.Member;

public class FightUserStats /*extends Codec<FightUserStats>*/ {
    public int hp = 500;
    public final Member user;
    private int exp = 0;

    public FightUserStats(Member user) {
        this.user = user;
    }

    public boolean addExp(int exp) {
        this.exp += exp;
        // TODO: Detect if levelled up
        return true;
    }

    public int getLvl() {
        return 1;
    }

}
