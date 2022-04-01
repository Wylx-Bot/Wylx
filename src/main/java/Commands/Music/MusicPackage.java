package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.ProcessPackage.ProcessPackage;

public class MusicPackage extends ProcessPackage {
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
            new LoopCommand(),
            new NowPlaying(),
        }, new SilentEvent[]{});
    }

    @Override
    public String getHeader() {
        return "Commands fow using Uwylx to pway music in youw sewvew";
    }
}
