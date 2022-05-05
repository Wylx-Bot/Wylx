package com.wylxbot.wylx.Commands.TimeConversion;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.EventPackage;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;

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
