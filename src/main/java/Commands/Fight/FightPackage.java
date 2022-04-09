package Commands.Fight;

import Core.Events.Commands.ServerCommand;
import Core.Events.EventPackage;
import Core.Events.SilentEvents.SilentEvent;

public class FightPackage extends EventPackage {
    public FightPackage() {
        super(new ServerCommand[]{
                new FightCommand()
        }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "Four shalt thou not count, neither count thou two, excepting that thou then proceed to three";
    }
}
