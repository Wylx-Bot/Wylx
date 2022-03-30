package Commands.Music;

import Core.Events.Commands.ServerCommand;
import Core.Events.SilentEvents.SilentEvent;
import Core.Events.EventPackage;

public class MusicPackage extends EventPackage {
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
        }, new SilentEvent[]{},
                true);
    }

    @Override
    public String getHeader() {
        return "Commands for using Wylx to play music in your server";
    }
}
