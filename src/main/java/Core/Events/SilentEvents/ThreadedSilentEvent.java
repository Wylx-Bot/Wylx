package Core.Events.SilentEvents;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedSilentEvent extends SilentEvent {
    private final long timeout;

    public ThreadedSilentEvent(String description){
        this(description, 300000);
    }

    public ThreadedSilentEvent(String description, long timeout){
        super(description);
        this.timeout = timeout;
    }

    @Override
    public final void runEvent(MessageReceivedEvent event, String prefix) {
        Timer timer = new Timer();
        Thread thread = new Thread(() -> {
            runEventThread(event, prefix);
            timer.cancel();
        });
        thread.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(thread.isAlive()){
                    thread.stop();
                }
            }
        }, timeout);
    }

    protected abstract void runEventThread(MessageReceivedEvent event, String prefix);
}
