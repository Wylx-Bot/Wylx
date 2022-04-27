package Commands.Music;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VolumeCommand extends ServerCommand {
    private static final String USAGE = "Usage: %{p}volume <number between 0 and 100>";

    VolumeCommand () {
        super("volume",
                CommandPermission.EVERYONE,
                "Set playback volume\n" + USAGE,
                "v");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();

        if (args.length != 2) {
            String message = String.format("Current volume is: %d\n%s",
                    ctx.musicManager().getVolume(),
                    ServerCommand.replacePrefix(USAGE, ctx.prefix()));
            event.getChannel().sendMessage(message).queue();
            return;
        }

        if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (number > 100 || number < 0) {
            event.getChannel().sendMessage("Volume out of range, keep it between 0-100").queue();
            return;
        }

        ctx.db().setSetting(ctx.guildID(), ServerIdentifiers.MusicVolume, number);
        ctx.musicManager().setVolume(number);
    }
}
