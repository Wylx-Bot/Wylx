package com.wylxbot.wylx.Core.Events.Commands;

import net.dv8tion.jda.api.Permission;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedCommand extends ServerCommand{
    private final long timeout;

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, String description, String ... aliases) {
        this(keyword, cmdPerm, description,300000, aliases);
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, String description, long timeout, String ... aliases) {
        super (keyword, cmdPerm, description, aliases);
        this.timeout = timeout;
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description, String ... aliases) {
        this(keyword, cmdPerm, perm, description, 300000, aliases);
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description, long timeout, String ... aliases) {
        super(keyword, cmdPerm, perm, description, aliases);
        this.timeout = timeout;
    }

    @Override
    public final void runCommand(CommandContext ctx) {
        Timer timer = new Timer();
        Thread thread = new Thread(() -> {
            runCommandThread(ctx);
            timer.cancel();
        });
        thread.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(thread.isAlive()){
                    thread.stop();
                    ctx.event().getMessage().reply("error: command took to long").queue();
                }
            }
        }, timeout);
    }

    protected abstract void runCommandThread(CommandContext ctx);
}
