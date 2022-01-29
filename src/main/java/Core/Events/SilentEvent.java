package Core.Events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SilentEvent {
	private final String description;

	@Deprecated
	public SilentEvent(){
		this("Some Dev is using deprecated features");
	}

	public SilentEvent(String description){
		this.description = description;
	}

	public abstract boolean check(MessageReceivedEvent event);
	public abstract void runEvent(MessageReceivedEvent event);

	public String getName(){
		return this.getClass().getSimpleName();
	}
	public String getDescription(){
		return description;
	}
}
