package Core.Events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface SilentEvent {
	boolean check(MessageReceivedEvent event);
	void runEvent(MessageReceivedEvent event);
}
