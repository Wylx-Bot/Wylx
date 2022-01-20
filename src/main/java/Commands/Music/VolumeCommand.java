package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class VolumeCommand extends ServerCommand {
    VolumeCommand () {
        super("volume", CommandPermission.EVERYONE);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var guildID = event.getGuild().getIdLong();
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());

        if (args.length != 2) {
            event.getChannel().sendMessage("Usage: $volume <number out of 100>").queue();
            return;
        }

        if (!MusicUtils.canUseVoiceCommand(guildID, event.getAuthor().getIdLong())) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (number > 100 || number < 0) {
            event.getChannel().sendMessage("Volume out of range, keep it between 0-100").queue();
            return;
        }

        manager.setVolume(number);
    }
}