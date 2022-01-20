package Core.Processing;

import Core.Music.GuildMusicManager;
import Core.Music.WylxPlayerManager;
import Core.Wylx;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoiceChannelProcessing extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VoiceChannelProcessing.class);

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        checkVoiceChannel(event.getGuild().getIdLong());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        checkVoiceChannel(event.getGuild().getIdLong());
    }

    private void checkVoiceChannel(long guildID) {
        GuildMusicManager guildMusicManager = WylxPlayerManager.getInstance().getGuildManager(guildID);
        AudioManager manager =  Wylx.getInstance().getGuildAudioManager(guildID);
        AudioChannel channel = manager.getConnectedChannel();

        if (channel == null) {
            guildMusicManager.stop();
            return;
        };

        // Leave if we are the only user left
        if (manager.isConnected() && channel.getMembers().size() == 1) {
            logger.debug("Leaving {} due to inactivity", guildID);
            guildMusicManager.stop();
        }
    }


}
