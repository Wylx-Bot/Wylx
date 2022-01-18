package Commands.Management;

import Core.Commands.CommandPackage;
import Core.Events.SilentEvent;
import Core.Commands.ServerCommand;

public class ManagementPackage extends CommandPackage {

	public ManagementPackage() {
		super(new ServerCommand[]{
				new SystemCommand(),
				new PingCommand(),},
				new SilentEvent[]{});
	}

	@Override
	public String getDescription() {
		return null;
	}
}
