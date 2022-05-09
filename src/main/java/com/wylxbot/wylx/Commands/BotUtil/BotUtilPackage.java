package com.wylxbot.wylx.Commands.BotUtil;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

public class BotUtilPackage extends EventPackage {
    public BotUtilPackage() {
        super(
                new ServerCommand[]{
                        new PingCommand(),
                        new RestartCommand(),
                        new StatusCommand(),
                        new UpdateCommand(),
                        new Help(),
                        new InviteCommand(),
                        new ServerInviteCommand()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "com.wylxbot.wylx.Commands for interacting with and checking up on Wylx itself";
    }
}
