package Core.Commands;

public abstract class CommandPackage {
	protected ServerCommand[] commands;

	protected CommandPackage(){
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
