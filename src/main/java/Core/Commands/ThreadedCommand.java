package Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedCommand extends ServerCommand{
	private final long timeout;

	public ThreadedCommand(){
		this(null);
	}

	public ThreadedCommand(String keyword){
		this(keyword, 300000);
	}

	public ThreadedCommand(String keyword, long timeout){
		this(keyword, false, timeout);
	}

	public ThreadedCommand(String keyword, boolean admin, long timeout) {
		this(keyword, admin, false, timeout);
	}

	public ThreadedCommand(String keyword, boolean admin, boolean beta, long timeout) {
		super(keyword, admin, beta);
		this.timeout = timeout;
	}

	@Override
	public final void runCommand(MessageReceivedEvent event) {
		Thread thread = new Thread(() -> runCommandThread(event));
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(thread.isAlive()){
					thread.stop();
					event.getMessage().reply("error: command took to long").queue();
				}
			}
		}, timeout);
	}

	protected abstract void runCommandThread(MessageReceivedEvent event);
}
