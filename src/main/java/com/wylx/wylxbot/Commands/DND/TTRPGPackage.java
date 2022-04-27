package com.wylx.wylxbot.Commands.DND;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

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
