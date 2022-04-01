package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Util.Helper;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class NowPlaying extends ServerCommand {
    NowPlaying() {
        super("nowplaying",
                CommandPermission.EVERYONE,
                "Show cuwwentwy pwaying song",
                "np");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Uwylx ish not pwaying music wight now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("U awe not in da same channew as da bot!").queue();
        } else {
            MessageEmbed embed = MusicUtils.createPlayingEmbed(ctx.musicManager().getCurrentTrack(),
                    "Pwaying %s", true);

            Helper.selfDestructingMsg(event.getChannel().sendMessageEmbeds(embed), Duration.ofMinutes(1));
        }
    }
}
