package com.wylx.wylxbot.Commands.TimeConversion;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.EventPackage;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;

public class TimePackage extends EventPackage {
    public TimePackage() {
        super(
                new ServerCommand[]{
                    new SetTimezone(),
                }, new SilentEvent[]{
                    new ConvertTime(),
                }
        );
    }

    @Override
    public String getHeader() {
        return "Converts time across timezones and helps with event planning";
    }
}
