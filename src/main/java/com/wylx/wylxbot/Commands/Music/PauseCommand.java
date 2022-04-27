package com.wylx.wylxbot.Commands.Music;

import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Music.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends ServerCommand {
    PauseCommand() {
        super("pause",
                CommandPermission.EVERYONE,
                "Pause current song");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            ctx.musicManager().pause(true);
        }
    }
}
