package com.wylxbot.wylx.Commands.ServerSettings;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

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
