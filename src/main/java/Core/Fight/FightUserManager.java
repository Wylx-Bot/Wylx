package Core.Fight;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FightUserManager {

    private final HashMap<Long, Boolean> fightMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public boolean userIsFighting(User user) {
        lock.lock();
        boolean result = fightMap.getOrDefault(user.getIdLong(), false);
        lock.unlock();
        return result;
    }

    public void serverIsFighting(long serverId, boolean isFighting) {
        lock.lock();
        fightMap.put(serverId, isFighting);
        lock.unlock();
    }

}
