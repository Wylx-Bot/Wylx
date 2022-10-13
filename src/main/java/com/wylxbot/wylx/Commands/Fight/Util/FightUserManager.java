package com.wylxbot.wylx.Commands.Fight.Util;

import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FightUserManager {

    private final static FightUserManager INSTANCE = new FightUserManager();

    private final HashMap<Long, UserFightStatus> fightMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private FightUserManager(){}

    public static FightUserManager getInstance(){
        return INSTANCE;
    }

    public UserFightStatus getUserStatus(Member user) {
        lock.lock();
        UserFightStatus result = fightMap.getOrDefault(user.getIdLong(), UserFightStatus.NONE);
        lock.unlock();
        return result;
    }

    public void setUserFightStatus(Member user, UserFightStatus status) {
        lock.lock();
        fightMap.put(user.getIdLong(), status);
        lock.unlock();
    }

}
