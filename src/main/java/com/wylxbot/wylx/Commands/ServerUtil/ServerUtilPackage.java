package com.wylxbot.wylx.Commands.ServerUtil;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

public class ServerUtilPackage extends EventPackage {
    public ServerUtilPackage() {
        super(
                new ServerCommand[]{
                        new CleanCommand(),
                        new ClearCommand(),
                        new ClearToCommand(),
                        new RepeatCommand()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "For managing and manipulating discord servers";
    }
}
