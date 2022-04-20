package Core.Events.SilentEvents;

import Core.Events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SilentEvent extends Event {
    private final String description;

    @Deprecated
    public SilentEvent(){
        this("Some Dev is using deprecated features");
    }

    public SilentEvent(String description){
        this.description = description;
    }

    public abstract boolean check(MessageReceivedEvent event, String prefix);
    public abstract void runEvent(MessageReceivedEvent event, String prefix);

    @Override
    public String getKeyword(){
        return this.getClass().getSimpleName().toLowerCase();
    }
    @Override
    public String getDescription(String alias){
        return description;
    }
    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
