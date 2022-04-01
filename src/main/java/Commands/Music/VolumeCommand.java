package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VolumeCommand extends ServerCommand {
    private static final String USAGE = "Uwsage: %{p}vowume <numbew between 0 and 100>";

    VolumeCommand () {
        super("vwowume",
                CommandPermission.EVERYONE,
                "Set pwayback vowume\n" + USAGE,
                "v", "volume");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();

        if (args.length != 2) {
            String message = String.format("Cuwwent vowume ish: %d\n%s",
                    ctx.musicManager().getVolume(),
                    ServerCommand.replacePrefix(USAGE, ctx.prefix()));
            event.getChannel().sendMessage(message).queue();
            return;
        }

        if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("U awe not in da same channew as da bot!").queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (number > 100 || number < 0) {
            event.getChannel().sendMessage("Vowume out of wange, keep it between 0-100").queue();
            return;
        }

        ctx.db().setSetting(ServerIdentifiers.MusicVolume, number);
        ctx.musicManager().setVolume(number);
    }
}
