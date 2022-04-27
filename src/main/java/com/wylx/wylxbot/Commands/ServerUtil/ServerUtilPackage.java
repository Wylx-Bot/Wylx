package com.wylx.wylxbot.Commands.ServerUtil;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

public class ServerUtilPackage extends EventPackage {
    public ServerUtilPackage() {
        super(
                new ServerCommand[]{
                        new CleanCommand(),
                        new ClearCommand(),
                        new ClearToCommand(),
                        new InviteCommand(),
                        new RepeatCommand()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "For managing and manipulating discord servers";
    }
}
