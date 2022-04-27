package com.wylx.wylxbot.Commands.BotUtil;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

public class BotUtilPackage extends EventPackage {
    public BotUtilPackage() {
        super(
                new ServerCommand[]{
                        new PingCommand(),
                        new RestartCommand(),
                        new SystemCommand(),
                        new UpdateCommand(),
                        new Help()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "com.wylx.wylxbot.Commands for interacting with and checking up on Wylx itself";
    }
}
