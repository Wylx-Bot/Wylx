package Commands.ServerSettings;

import Core.Events.Commands.ServerCommand;
import Core.Events.SilentEvents.SilentEvent;
import Core.Events.EventPackage;

public class ServerSettingsPackage extends EventPackage {
    public ServerSettingsPackage() {
        super(
                new ServerCommand[]{
                        new SetPrefix(),
                        new EnableCommand(),
                        new EnablePackage()
                }, new SilentEvent[]{},
                true
        );
    }

    @Override
    public String getHeader() {
        return "Server specific settings for Wylx";
    }
}
