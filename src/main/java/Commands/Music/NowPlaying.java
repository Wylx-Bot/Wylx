package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.entities.Message;
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
    public void runCommand(MessageReceivedEvent event, CommandContext ctx) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(ctx.guildID());
        long memberID = event.getAuthor().getIdLong();

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(ctx.guildID(), memberID)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            MessageEmbed embed = MusicUtils.createPlayingEmbed(manager.getCurrentTrack(), "Playing %s", true);
            event.getChannel().sendMessageEmbeds(embed)
                    .delay(Duration.ofSeconds(60))
                    .flatMap(Message::delete)
                    .queue();
        }
    }
}
