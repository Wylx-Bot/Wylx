package Commands.Frog;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

public class FrogPackage extends ProcessPackage {

    public FrogPackage() {
        super(new ServerCommand[]{
                new BonkCommand(),
                new FrogFactCommand(),
                new DrawFrogCardCommand(),
                new ValidateCommand(),
                new PetCommand(),
        }, new SilentEvent[]{});
    }

    @Override
    public String getHeader() {
        return "Fwog facts, fwog pwaying cawds, and othew whowesome things";
    }
}
