package Commands.Management;

import Core.Commands.CommandPackage;

public class ManagementPackage extends CommandPackage {

	public ManagementPackage(){
		this.commands = new Core.Commands.ServerCommand[]{new SystemCommand("system")};
	}

	@Override
	public String getDescription() {
		return null;
	}
}
