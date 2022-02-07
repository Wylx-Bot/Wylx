package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
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
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        if (ctx.musicManager().isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            MessageEmbed embed = MusicUtils.createPlayingEmbed(ctx.musicManager().getCurrentTrack(),
                    "Playing %s", true);

            event.getChannel().sendMessageEmbeds(embed)
                    .delay(Duration.ofSeconds(60))
                    .flatMap(Message::delete)
                    .queue();
        }
    }
}
