package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicSeek;
import Core.Music.MusicUtils;
import Core.Util.Helper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class SeekCommand extends ServerCommand {
    SeekCommand() {
        super("sweek",
                CommandPermission.EVERYONE,
                """
                        Sweek to wocation in cuwwent twack. U can use + ow - to seek wewative to cuwwent pwace
                        
                        Uwsage:
                        %{p}sweek Sweconds
                        %{p}sweek HH:MM:SS OW MM:SS>
                        %{p}sweek +/-Seweconds OW +/- HH:MM:SS OW +/- MM:SS
                        """, "seek");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.args().length != 2) {
            return;
        }

        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Uwylx ish not pwaying music wight now!").queue();
            return;
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("U awe not in da same channew as da bot!").queue();
            return;
        }

        MusicSeek seekLoc = MusicUtils.getDurationFromArg(ctx.args()[1]);

        if (seekLoc == null) {
            event.getChannel().sendMessage(getDescription(ctx.prefix())).queue();
            return;
        }

        String prettyTime = MusicUtils.getPrettyDuration(seekLoc.dur());
        ctx.musicManager().seek(seekLoc);
        String msg;
        if (seekLoc.relative()) {
            msg = String.format("Seweking %s", prettyTime);
        } else {
            msg = String.format("Nowo pwaying awt %s", prettyTime);
        }

        Helper.selfDestructingMsg(event.getChannel().sendMessage(msg), Duration.ofMinutes(1));
    }
}
