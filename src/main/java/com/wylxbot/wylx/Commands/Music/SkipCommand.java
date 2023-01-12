package com.wylxbot.wylx.Commands.Music;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Music.MusicUtils;
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
        MusicUtils.VoiceCommandBlockedReason blocked = MusicUtils.voiceCommandBlocked(ctx);
        if (blocked != MusicUtils.VoiceCommandBlockedReason.COMMAND_OK) {
            event.getChannel().sendMessage(blocked.reason).queue();
            return;
        }

        ctx.musicManager().skip();
    }
}
