package com.wylx.wylxbot.Core.Processing;

import com.wylx.wylxbot.Core.Music.GuildMusicManager;
import com.wylx.wylxbot.Core.Music.WylxPlayerManager;
import com.wylx.wylxbot.Core.Wylx;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes voice channel events, mostly used to detect when Wylx should leave a voice channel.
 */
public class VoiceChannelProcessing extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VoiceChannelProcessing.class);

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        checkVoiceChannel(event.getGuild().getId());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        checkVoiceChannel(event.getGuild().getId());
    }

    private void checkVoiceChannel(String guildId) {
        WylxPlayerManager playerManager = WylxPlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildManager(guildId);
        AudioManager manager =  Wylx.getInstance().getGuildAudioManager(guildId);
        AudioChannel channel = manager.getConnectedChannel();

        if (channel == null) {
            guildMusicManager.stop();
            return;
        }

        // Leave if we are the only user left
        if (manager.isConnected() && channel.getMembers().size() == 1) {
            logger.debug("Leaving {} due to inactivity", guildId);
            guildMusicManager.stop();
        }
    }
}
