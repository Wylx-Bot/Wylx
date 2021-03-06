package com.wylxbot.wylx.Core.Events;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class EventPackage {
    private final ServerCommand[] commands;
    private final SilentEvent[] silentEvents;
    private final Event[] events;

    public EventPackage(ServerCommand[] commands, SilentEvent[] silentEvents){
        this.commands = commands;
        this.silentEvents = silentEvents;
        this.events = Stream.concat(Arrays.stream(commands), Arrays.stream(silentEvents)).toArray(Event[]::new);
    }

    public String getName(){
        return this.getClass().getSimpleName();
    }

    public abstract String getHeader();

    public final String getDescription(ServerEventManager eventManager){
        StringBuilder descriptionBuilder = new StringBuilder();
        descriptionBuilder.append("+ ");
        boolean packageEnabled = eventManager.checkPackage(this);
        descriptionBuilder.append(packageEnabled ? "" : "(Disabled) ");
        descriptionBuilder.append(getName());
        descriptionBuilder.append(" - ");
        descriptionBuilder.append(getHeader()).append("\n");

        for(Event event : events){
            descriptionBuilder.append("-\t");
            descriptionBuilder.append(event.getKeyword());
            if(event.getAliases().length > 0){
                descriptionBuilder.append(" (aka: ");
                String[] aliases = event.getAliases();
                for(int i = 0; i < aliases.length - 1; i++){
                    descriptionBuilder.append(aliases[i]).append(", ");
                }
                descriptionBuilder.append(aliases[aliases.length - 1]).append(")");
            }
            boolean eventEnabled = eventManager.checkEvent(event);
            if(eventEnabled != packageEnabled) descriptionBuilder.append(eventEnabled ? " (Enabled) " : " (Disabled) ");
            descriptionBuilder.append("\n");
        }

        return descriptionBuilder.toString();
    }

    public ServerCommand[] getCommands() {
        return commands;
    }

    public SilentEvent[] getSilentEvents() {
        return silentEvents;
    }

    public Event[] getEvents(){
        return events;
    }
}
