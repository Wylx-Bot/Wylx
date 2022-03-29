package Commands.DND;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.Processing.ProcessPackage;

public class TTRPGPackage extends ProcessPackage {
    public TTRPGPackage() {
        super(new ServerCommand[]{
                new MathCommand()
        }, new SilentEvent[]{
                new DiceRoll()
        });
    }

    @Override
    public String getHeader() {
        return "Useful for TTRPG sessions and environments";
    }
}
