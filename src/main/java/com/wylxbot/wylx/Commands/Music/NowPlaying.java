package com.wylxbot.wylx.Commands.Music;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Music.MusicUtils;
import com.wylxbot.wylx.Core.Util.Helper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class NowPlaying extends ServerCommand {
    NowPlaying() {
        super("nowplaying",
                CommandPermission.EVERYONE,
                "Show currently playing song",
                "np");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        MusicUtils.VoiceCommandBlockedReason blocked = MusicUtils.voiceCommandBlocked(ctx);
        if (blocked != MusicUtils.VoiceCommandBlockedReason.COMMAND_OK) {
            event.getChannel().sendMessage(blocked.reason).queue();
            return;
        }

        MessageEmbed embed = MusicUtils.createPlayingEmbed(ctx.musicManager().getCurrentTrack(),
                "Playing %s", true);

        Helper.selfDestructingMsg(event.getChannel().sendMessageEmbeds(embed), Duration.ofMinutes(1));
    }
}
