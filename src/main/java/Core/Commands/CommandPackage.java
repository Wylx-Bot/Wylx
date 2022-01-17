package Core.Commands;

public abstract class CommandPackage {
	private final ServerCommand[] commands;

	private CommandPackage(){
		commands = null;
	}

	public ServerCommand[] getCommands(){
		return commands;
	}

	public String getName(){
		return this.getClass().getSimpleName();
	}

	public abstract String getDescription();
}
