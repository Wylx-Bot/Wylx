package Commands.DND;

import Core.Events.Commands.ServerCommand;
import Core.Events.SilentEvents.SilentEvent;
import Core.Events.EventPackage;

public class TTRPGPackage extends EventPackage {
    public TTRPGPackage() {
        super(
                new ServerCommand[]{
                    new MathCommand()
                }, new SilentEvent[]{
                    new DiceRoll()
                }
		);
    }

    @Override
    public String getHeader() {
        return "Useful for TTRPG sessions and environments";
    }
}
