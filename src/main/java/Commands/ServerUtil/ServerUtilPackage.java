package Commands.ServerUtil;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

public class ServerUtilPackage extends ProcessPackage {
    public ServerUtilPackage() {
        super(
                new ServerCommand[]{
                        new CleanCommand(),
                        new ClearCommand(),
                        new ClearToCommand(),
                        new InviteCommand(),
                        new RepeatCommand(),
                        new SetPrefix()
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "For managing and manipulating discord servers";
    }
}
