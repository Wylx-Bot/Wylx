package Commands.TimeConversion;

import Core.Events.Commands.ServerCommand;
import Core.Events.EventPackage;
import Core.Events.SilentEvents.SilentEvent;

public class TimePackage extends EventPackage {
    public TimePackage() {
        super(
                new ServerCommand[]{
                    new SetTimezone()
                }, new SilentEvent[]{

                }
        );
    }

    @Override
    public String getHeader() {
        return "Converts time across timezones and helps with event planning";
    }
}
