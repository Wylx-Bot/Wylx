package com.wylxbot.wylx.Commands.DND;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

public class TTRPGPackage extends EventPackage {
    public TTRPGPackage() {
        super(
                new ServerCommand[]{
                    new MathCommand(),
                    new SpellCommand(),
                    new LaTexCommand()
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
