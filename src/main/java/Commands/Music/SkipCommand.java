package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkipCommand extends ServerCommand {
    public SkipCommand() {
        super("skip",
                CommandPermission.EVERYONE,
                "Stop current track and play next in queue");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, CommandContext ctx) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());
        long memberID = event.getAuthor().getIdLong();
        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(ctx.guildID(), memberID)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            manager.skip();
        }
    }
}
