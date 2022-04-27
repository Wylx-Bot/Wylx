package com.wylx.wylxbot.Commands.Music;

import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Events.EventPackage;

public class MusicPackage extends EventPackage {
    public MusicPackage() {
        super(
                new ServerCommand[]{
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
                }, new SilentEvent[]{}
		);
    }

    @Override
    public String getHeader() {
        return "com.wylx.wylxbot.Commands for using Wylx to play music in your server";
    }
}
