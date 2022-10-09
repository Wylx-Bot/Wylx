package com.wylxbot.wylx.Commands.Frog;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

public class FrogPackage extends EventPackage {

    public FrogPackage() {
        super(
                new ServerCommand[]{
                    new BonkCommand(),
                    new FrogFactCommand(),
                    new DrawFrogCardCommand(),
                    new ValidateCommand()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "Frog facts, frog playing cards, and other wholesome things";
    }
}
