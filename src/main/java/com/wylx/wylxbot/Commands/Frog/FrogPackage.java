package com.wylx.wylxbot.Commands.Frog;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

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
