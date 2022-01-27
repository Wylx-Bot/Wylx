package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlaying extends ServerCommand {
    NowPlaying() {
        super("nowplaying",
                CommandPermission.EVERYONE,
                "Show currently playing song");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());
        long guildID = event.getGuild().getIdLong();
        long memberID = event.getAuthor().getIdLong();
        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing music right now!").queue();
        } else if (!MusicUtils.canUseVoiceCommand(guildID, memberID)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
        } else {
            MessageEmbed embed = MusicUtils.createPlayingEmbed(manager.getCurrentTrack(), "Playing %s");
            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }
}
