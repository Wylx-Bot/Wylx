package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand extends ServerCommand {
    LoopCommand() {
        super("woop",
                CommandPermission.EVERYONE,
                "Loop cuwwent twack\n" +
                        "usage: %{p}woop <twue/yes or fawse/no>", "loop");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        GuildMusicManager manager = ctx.musicManager();
        String[] args = ctx.args();

        if (args.length != 2) {
            event.getChannel().sendMessage(getDescription(ctx.prefix())).queue();
            return;
        }

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Uwylx ish not pwaying music wight now!").queue();
            return;
        } else if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("U awe not in da same channew as da bot!").queue();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "yes", "true" -> {
                manager.loop(true);
                String msg = String.format("Looping cuwwent song. Use %swoop fawse to disabwe", ctx.prefix());
                event.getChannel().sendMessage(msg).queue();
            }
            case "no", "false" -> {
                manager.loop(false);
                event.getChannel().sendMessage("Disabwed wooping").queue();
            }
            default -> event.getChannel().sendMessage(getDescription(ctx.prefix())).queue();
        }
    }
}
