package Core.Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ThreadedCommand extends ServerCommand{
	private final long timeout;

	public ThreadedCommand(String keyword, CommandPermission cmdPerm) {
		this(keyword, cmdPerm, 300000);
	}

	public ThreadedCommand(String keyword, CommandPermission cmdPerm, long timeout) {
		super (keyword, cmdPerm);
		this.timeout = timeout;
	}

	public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm) {
		this(keyword, cmdPerm, perm, 300000);
	}

	public ThreadedCommand(String keyword, CommandPermission cmdPerm, Permission perm, long timeout) {
		super(keyword, cmdPerm, perm);
		this.timeout = timeout;
	}

	@Override
	public final void runCommand(MessageReceivedEvent event, String[] args) {
		Thread thread = new Thread(() -> runCommandThread(event, args));
		Timer timer = new Timer();

		thread.start();
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

	protected abstract void runCommandThread(MessageReceivedEvent event, String[] args);
}
