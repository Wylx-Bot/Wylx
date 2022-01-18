package Core.Commands;

import Core.Events.SilentEvent;

public abstract class CommandPackage {
	protected ServerCommand[] commands;
	protected SilentEvent[] events;

	public CommandPackage(ServerCommand[] commands, SilentEvent[] events){
		this.commands = commands;
		this.events = events;
	}

	public ServerCommand[] getCommands(){
		return commands;
	}

	public SilentEvent[] getEvents() {
		return events;
	}

	public String getName(){
		return this.getClass().getSimpleName();
	}

	public abstract String getDescription();
}
