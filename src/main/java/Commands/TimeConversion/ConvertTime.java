package Commands.TimeConversion;

import Core.Events.SilentEvents.SilentEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ConvertTime extends SilentEvent {

    public ConvertTime(){
        super("Automagically detects messages containing a time and converts them to timezones for server users");
    }

    @Override
    public boolean check(MessageReceivedEvent event, String prefix) {
        return false;
    }

    // TODO: Only convert to the timezones that people in the server have
    @Override
    public void runEvent(MessageReceivedEvent event, String prefix) {

    }
}
