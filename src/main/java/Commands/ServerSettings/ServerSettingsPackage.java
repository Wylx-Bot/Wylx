package Commands.ServerSettings;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.Processing.ProcessPackage;

public class ServerSettingsPackage extends ProcessPackage {
    public ServerSettingsPackage() {
        super(
                new ServerCommand[]{
                        new SetPrefix()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "Server specific settings for Wylx";
    }
}
