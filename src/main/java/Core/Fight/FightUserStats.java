package Core.Fight;

import net.dv8tion.jda.api.entities.User;

public class FightUserStats /*extends Codec<FightUserStats>*/ {
    public int hp = 500;
    public final User user;

    public FightUserStats(User user) {
        this.user = user;
    }

}
