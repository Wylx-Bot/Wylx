package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand extends ServerCommand {
    LoopCommand() {
        super("loop",
                CommandPermission.EVERYONE,
                "Loop current track\nUsage: %{p}loop <true/yes OR false/no>");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, CommandContext ctx) {
        GuildMusicManager manager = ctx.musicManager();
        String[] args = ctx.args();

        if (args.length != 2) {
            event.getChannel().sendMessage("Usage: $loop <true OR false>").queue();
            return;
        }

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
            return;
        } else if (!MusicUtils.canUseVoiceCommand(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "yes", "true" -> {
                manager.loop(true);
                event.getChannel().sendMessage("Looping current song. Use $loop false to disable").queue();
            }
            case "no", "false" -> {
                manager.loop(false);
                event.getChannel().sendMessage("Disabled looping").queue();
            }
            default -> event.getChannel().sendMessage("Unknown argument").queue();
        }
    }
}
