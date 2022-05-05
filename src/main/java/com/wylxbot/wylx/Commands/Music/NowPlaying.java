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
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            MessageEmbed embed = MusicUtils.createPlayingEmbed(ctx.musicManager().getCurrentTrack(),
                    "Playing %s", true);

            Helper.selfDestructingMsg(event.getChannel().sendMessageEmbeds(embed), Duration.ofMinutes(1));
        }
    }
}
