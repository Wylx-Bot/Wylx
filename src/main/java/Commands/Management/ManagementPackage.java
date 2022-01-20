package Commands.Management;

import Core.Commands.CommandPackage;
import Core.Events.SilentEvent;
import Core.Commands.ServerCommand;
import Core.ProcessPackage.ProcessPackage;

public class ManagementPackage extends ProcessPackage {

	public ManagementPackage() {
		super(new ServerCommand[]{
				new SystemCommand(),
				new PingCommand(),
				new RepeatCommand()},
				new SilentEvent[]{});
	}

	@Override
	public String getHeader() {
		return "Server Management Commands";
	}
}
