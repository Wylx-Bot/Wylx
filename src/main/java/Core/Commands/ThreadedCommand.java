package Core.Commands;

import net.dv8tion.jda.api.Permission;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedCommand extends ServerCommand{
    private final long timeout;

    @Deprecated
    public ThreadedCommand(String keyword, CommandPermission cmdPerm) {
        this(keyword, cmdPerm, "Some Dev is using deprecated features");
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, String description) {
        this(keyword, cmdPerm, description,300000);
    }

    @Deprecated
    public ThreadedCommand(String keyword, CommandPermission cmdPerm, long timeout) {
        this(keyword, cmdPerm, "Some Dev is using deprecated features", timeout);
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, String description, long timeout) {
        super (keyword, cmdPerm, description);
        this.timeout = timeout;
    }

    @Deprecated
    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm) {
        this(keyword, cmdPerm, perm, "Some Dev is using deprecated features");
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description) {
        this(keyword, cmdPerm, perm, description, 300000);
    }

    @Deprecated
    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, long timeout) {
        this(keyword, cmdPerm, perm, "Some Dev is using deprecated features", timeout);
    }

    public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description, long timeout) {
        super(keyword, cmdPerm, perm, description);
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
