package Commands.BotUtil;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

public class BotUtilPackage extends ProcessPackage {
    public BotUtilPackage() {
        super(
                new ServerCommand[]{
                        new PingCommand(),
                        new RestartCommand(),
                        new SystemCommand(),
                        new UpdateCommand()
                }, new SilentEvent[]{});
    }

    @Override
    public String getHeader() {
        return "Commawnds fow intewacting with awnd checking uwp owon UwUywx itsewf uwu in case uwu wawnt tuwu know how i'm doing wight now";
    }
}
