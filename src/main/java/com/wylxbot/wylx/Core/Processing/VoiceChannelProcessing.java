package com.wylxbot.wylx.Core.Processing;

import com.wylxbot.wylx.Core.Music.GuildMusicManager;
import com.wylxbot.wylx.Core.Music.WylxPlayerManager;
import com.wylxbot.wylx.Wylx;
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
        checkVoiceChannel(event.getGuild().getId());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        checkVoiceChannel(event.getGuild().getId());
    }

    private void checkVoiceChannel(String guildID) {
        GuildMusicManager guildMusicManager = WylxPlayerManager.getInstance().getGuildManager(guildID);
        AudioManager manager =  Wylx.getInstance().getGuildAudioManager(guildID);
        AudioChannel channel = manager.getConnectedChannel();

        if (channel == null) {
            guildMusicManager.stop();
            return;
        }

        // Leave if we are the only user left
        if (manager.isConnected() && channel.getMembers().size() == 1) {
            logger.debug("Leaving {} due to inactivity", guildID);
            guildMusicManager.stop();
        }
    }


}
