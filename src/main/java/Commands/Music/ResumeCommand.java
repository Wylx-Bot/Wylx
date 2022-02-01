package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
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
        var manager = WylxPlayerManager.getInstance().getGuildManager(ctx.guildID());
        long memberID = event.getAuthor().getIdLong();
        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            manager.pause(false);
        }
    }
}
