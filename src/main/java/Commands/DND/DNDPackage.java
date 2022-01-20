package Commands.DND;

import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;

public class DNDPackage extends CommandPackage {
	public DNDPackage() {
		super(new ServerCommand[]{}, new SilentEvent[]{new DiceRoll()});
	}

	@Override
	public String getDescription() {
		return null;
	}
}
