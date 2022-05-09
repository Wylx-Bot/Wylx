package com.wylxbot.wylx.Commands.Roles.RolesUtil;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class DMListenerUserManager {
    private final HashMap<Long, DMListener> dmMap = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private DMListenerUserManager() {};
    private static final DMListenerUserManager listener = new DMListenerUserManager();
    public static DMListenerUserManager getInstance() {
        return listener;
    }

    public void addDMListener(User user, DMListener listener) {
        lock.lock();
        dmMap.put(user.getIdLong(), listener);
        user.getJDA().addEventListener(listener);
        lock.unlock();
    }

    public void removeDMListener(User user, DMListenerQuitReason reason) {
        lock.lock();
        DMListener listener = dmMap.remove(user.getIdLong());
        if (listener != null) {
            listener.quit(reason);
            user.getJDA().removeEventListener(listener);
        }
        lock.unlock();
    }
}
