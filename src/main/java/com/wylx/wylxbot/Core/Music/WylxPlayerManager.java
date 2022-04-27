package com.wylx.wylxbot.Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import java.util.concurrent.ConcurrentHashMap;

public class WylxPlayerManager {
    private final AudioPlayerManager playerManager;
    private final ConcurrentHashMap<String, GuildMusicManager> managers;

    private WylxPlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        managers = new ConcurrentHashMap<>();
    }

    private static final WylxPlayerManager instance = new WylxPlayerManager();
    public static WylxPlayerManager getInstance() {
        return instance;
    }

    public GuildMusicManager getGuildManager(String guildID) {
        if (managers.containsKey(guildID)) {
            return managers.get(guildID);
        }

        GuildMusicManager newManager = new GuildMusicManager(guildID, playerManager);
        managers.put(guildID, newManager);
        return newManager;
    }

    public void loadTracks (String token, GuildMusicManager musicManager, AudioLoadResultHandler resultHandler) {
        playerManager.loadItemOrdered(musicManager, token, resultHandler);
    }
}
