package com.wylxbot.wylx.Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.wylxbot.wylx.Wylx;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;

import java.util.concurrent.ConcurrentHashMap;

public class WylxPlayerManager {
    private final AudioPlayerManager playerManager;
    private final ConcurrentHashMap<String, GuildMusicManager> managers;

    private WylxPlayerManager() {
        playerManager = new DefaultAudioPlayerManager();

        // Use Youtube Source manager from youtube-source repo instead of built-in
        YoutubeAudioSourceManager ytSrcMgr = new YoutubeAudioSourceManager(
                /*allowSearch:*/ true,
                new AndroidMusicWithThumbnail(),
                new MusicWithThumbnail(),
                new TvHtml5EmbeddedWithThumbnail(),
                new WebEmbeddedWithThumbnail(),
                new WebWithThumbnail()
        );

        // Youtube source can be given a refresh token to prevent needing to go through the oAuth flow again.
        // If null is passed, then the user is required to follow the oAuth link found in the logs to login.
        var cfg = Wylx.getWylxConfig();
        ytSrcMgr.useOauth2(cfg.oauthRefreshToken, false);

        playerManager.registerSourceManager(ytSrcMgr);

        // Add rest of the build-in audio sources
        AudioSourceManagers.registerRemoteSources(
                playerManager,
                com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class
        );
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
