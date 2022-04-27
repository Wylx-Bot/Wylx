package com.wylx.wylxbot.Commands.ServerSettings;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

public class ServerSettingsPackage extends EventPackage {
    public ServerSettingsPackage() {
        super(
                new ServerCommand[]{
                        new SetPrefix(),
                        new EnableCommand(),
                        new EnablePackage()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "Server specific settings for Wylx";
    }
}
