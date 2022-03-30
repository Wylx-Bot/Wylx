package Commands.Frog;

import Core.Events.Commands.ServerCommand;
import Core.Events.SilentEvents.SilentEvent;
import Core.Events.EventPackage;

public class FrogPackage extends EventPackage {

    public FrogPackage() {
        super(new ServerCommand[]{
                new BonkCommand(),
                new FrogFactCommand(),
                new DrawFrogCardCommand(),
                new ValidateCommand()
        }, new SilentEvent[]{},
                false);
    }

    @Override
    public String getHeader() {
        return "Frog facts, frog playing cards, and other wholesome things";
    }
}
