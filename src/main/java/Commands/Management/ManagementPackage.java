package Commands.Management;

import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;

public class ManagementPackage extends CommandPackage {

	public ManagementPackage(){
		super(new ServerCommand[]{new SystemCommand("system")}, new SilentEvent[]{});
	}

	@Override
	public String getDescription() {
		return null;
	}
}
