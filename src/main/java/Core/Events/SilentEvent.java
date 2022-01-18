package Core.Events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SilentEvent {
	public abstract boolean check(MessageReceivedEvent event);
	public abstract void runEvent(MessageReceivedEvent event);

	public String getName(){
		return this.getClass().getSimpleName();
	}
	public abstract String getDescription();
}
