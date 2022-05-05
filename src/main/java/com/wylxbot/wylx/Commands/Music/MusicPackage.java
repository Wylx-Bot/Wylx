package com.wylxbot.wylx.Commands.Music;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Events.EventPackage;

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
        return "com.wylxbot.wylx.Commands for using Wylx to play music in your server";
    }
}
