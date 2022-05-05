package com.wylxbot.wylx.Commands.Fight.Util;

import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FightUserManager {

    private final HashMap<Long, Boolean> fightMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public boolean userIsFighting(Member user) {
        lock.lock();
        boolean result = fightMap.getOrDefault(user.getIdLong(), false);
        lock.unlock();
        return result;
    }

    public void setUserIsFighting(Member user, boolean isFighting) {
        lock.lock();
        fightMap.put(user.getIdLong(), isFighting);
        lock.unlock();
    }

}
