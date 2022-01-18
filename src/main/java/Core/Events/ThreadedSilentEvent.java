package Core.Events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedSilentEvent implements SilentEvent{
	private final long timeout;

	public ThreadedSilentEvent(){
		this(300000);
	}

	public ThreadedSilentEvent(long timeout){
		this.timeout = timeout;
	}

	@Override
	public final void runEvent(MessageReceivedEvent event) {
		Timer timer = new Timer();
		Thread thread = new Thread(() -> {
			runEventThread(event);
			timer.cancel();
		});
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(thread.isAlive()){
					thread.stop();
				}
			}
		}, timeout);
	}

	protected abstract void runEventThread(MessageReceivedEvent event);
}
