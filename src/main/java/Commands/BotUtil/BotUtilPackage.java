package Commands.BotUtil;

import Core.Events.Commands.ServerCommand;
import Core.Events.SilentEvents.SilentEvent;
import Core.Events.EventPackage;

public class BotUtilPackage extends EventPackage {
    public BotUtilPackage() {
        super(
                new ServerCommand[]{
                        new PingCommand(),
                        new RestartCommand(),
                        new SystemCommand(),
                        new UpdateCommand(),
                        new Help()
                }, new SilentEvent[]{},
                true);
    }

    @Override
    public String getHeader() {
        return "Commands for interacting with and checking up on Wylx itself";
    }
}
