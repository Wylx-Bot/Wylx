package Commands.ServerSettings;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

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
        return "Sewvew specific settings fow UWUywx... liwke what yuwu liwke hewre";
    }
}
