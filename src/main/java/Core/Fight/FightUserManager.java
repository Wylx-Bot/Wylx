package Core.Fight;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FightUserManager {

    private final HashMap<Long, Boolean> fightMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public boolean userIsFighting(long userId) {
        lock.lock();
        boolean result = fightMap.get(userId);
        lock.unlock();
        return result;
    }

    public void serverIsFighting(long serverId, boolean isFighting) {
        lock.lock();
        fightMap.put(serverId, isFighting);
        lock.unlock();
    }

}
