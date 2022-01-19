package Commands.Music;

import Core.Commands.CommandPackage;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;

public class MusicPackage extends CommandPackage {
    public MusicPackage() {
        super(new ServerCommand[]{
            new PlayCommand(),
            new SkipCommand(),
            new ClearPlaylistCommand(),
            new VolumeCommand(),
            new PauseCommand(),
            new ResumeCommand(),
            new QueueCommand(),
            new SeekCommand(),
        }, new SilentEvent[]{});
    }

    @Override
    public String getDescription() {
        return null;
    }
}
