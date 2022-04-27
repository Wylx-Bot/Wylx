package com.wylx.wylxbot.Commands.Music;

import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Music.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand extends ServerCommand {
    public SkipCommand() {
        super("skip",
                CommandPermission.EVERYONE,
                "Stop current track and play next in queue");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            ctx.musicManager().skip();
        }
    }
}
