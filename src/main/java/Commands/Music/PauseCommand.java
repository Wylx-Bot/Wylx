package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PauseCommand extends ServerCommand {
    PauseCommand() {
        super("pause",
                CommandPermission.EVERYONE,
                "Paws cuwwent song");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Uwlyx ish not pwaying music wight now!").queue();
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("U awe not in da same channew as da bot!").queue();
        } else {
            ctx.musicManager().pause(true);
        }
    }
}
