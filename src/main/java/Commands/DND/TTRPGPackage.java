package Commands.DND;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

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
        return "Uwuseful for TTRPG sessiowons and enviuwunments";
    }
}
