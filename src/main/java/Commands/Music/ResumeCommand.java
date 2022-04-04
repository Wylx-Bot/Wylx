package Commands.Music;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Music.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand extends ServerCommand {
    ResumeCommand() {
        super("resume",
                CommandPermission.EVERYONE,
                "Resume playback if paused");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            ctx.musicManager().pause(false);
        }
    }
}
