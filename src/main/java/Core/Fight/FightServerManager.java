package Core.Fight;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FightServerManager {

    private final HashMap<Long, Boolean> fightMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public boolean serverIsFighting(long serverId) {
        lock.lock();
        boolean result = fightMap.get(serverId);
        lock.unlock();
        return result;
    }

    public void setServerIsFighting(long serverId, boolean isFighting) {
        lock.lock();
        fightMap.put(serverId, isFighting);
        lock.unlock();
    }

}
