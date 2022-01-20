package Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import java.util.concurrent.ConcurrentHashMap;

public class WylxPlayerManager {
    private final AudioPlayerManager playerManager;
    private final ConcurrentHashMap<Long, GuildMusicManager> managers;

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

    public GuildMusicManager getGuildManager(long guildID) {
        if (managers.containsKey(guildID)) {
            return managers.get(guildID);
        }

        GuildMusicManager newManager = new GuildMusicManager(guildID, playerManager);
        managers.put(guildID, newManager);
        return newManager;
    }

    public void loadTracks (String token, long guildID, AudioLoadResultHandler resultHandler) {
        GuildMusicManager guildManager = getGuildManager(guildID);
        playerManager.loadItemOrdered(guildManager, token, resultHandler);
    }
}
