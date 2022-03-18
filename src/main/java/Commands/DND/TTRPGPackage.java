package Commands.DND;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

public class TTRPGPackage extends ProcessPackage {
    public TTRPGPackage() {
        super(new ServerCommand[]{
                new MathCommand(),
                new SpellCommand()
        }, new SilentEvent[]{
                new DiceRoll()
        });
    }

    @Override
    public String getHeader() {
        return "Useful for TTRPG sessions and environments";
    }
}
