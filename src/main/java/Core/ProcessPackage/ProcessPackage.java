package Core.ProcessPackage;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;

public abstract class ProcessPackage {
	private final ServerCommand[] commands;
	private final SilentEvent[] events;

	public ProcessPackage(ServerCommand[] commands, SilentEvent[] events){
		this.commands = commands;
		this.events = events;
	}

	public String getName(){
		return this.getClass().getSimpleName();
	}

	public abstract String getHeader();

	public final String getDescription(){
		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append("+");
		descriptionBuilder.append(getName()).append(" - ");
		descriptionBuilder.append(getHeader()).append("\n");

		for(ServerCommand command : commands){
			descriptionBuilder.append("-\t");
			descriptionBuilder.append(command.getKeyword());
			if(command.getAliases().length > 0){
				descriptionBuilder.append(" (aka: ");
				String[] aliases = command.getAliases();
				for(int i = 0; i < aliases.length - 1; i++){
					descriptionBuilder.append(aliases[i]).append(", ");
				}
				descriptionBuilder.append(aliases[aliases.length - 1]).append(")");
			}
			descriptionBuilder.append("\n");
		}
		for(SilentEvent event : events){
			descriptionBuilder.append("-\t");
			descriptionBuilder.append(event.getName());
			descriptionBuilder.append("\n");
		}

		return descriptionBuilder.toString();
	}

	public ServerCommand[] getCommands() {
		return commands;
	}

	public SilentEvent[] getEvents() {
		return events;
	}
}
