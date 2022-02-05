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
        super("seek",
                CommandPermission.EVERYONE,
                """
                        Seek to location in current track. You can use + or - to seek relative to current place
                        
                        Usage:
                        %{p}seek Seconds
                        %{p}seek HH:MM:SS OR MM:SS>
                        %{p}seek +/-Seconds OR +/- HH:MM:SS OR +/- MM:SS
                        """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.args().length != 2) {
            return;
        }

        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
            return;
        } else if (MusicUtils.canUseVoiceCommand(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
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
            msg = String.format("Seeking %s", prettyTime);
        } else {
            msg = String.format("Now playing at %s", prettyTime);
        }

        Helper.sendTemporaryMessage(event.getChannel().sendMessage(msg), Duration.ofMinutes(1));
    }
}
