package Core.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WylxPlayerManager {
    private final AudioPlayerManager playerManager;
    private final ConcurrentHashMap<Long, GuildMusicManager> managers;

    //TODO make an environment variable for this(?) and eventually a spotify account for wylx
    private final SpotifyApi spotifyApi;

    private WylxPlayerManager() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        spotifyApi = new SpotifyApi.Builder()
                .setClientId("648860ca8d704e4191037e7ef5ac7b34")
                .setClientSecret("05c19ce2725843fd9c9db52c011af633")
                .build();
        spotifyClientCredentials();

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

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public void loadTracks (String token, GuildMusicManager musicManager, AudioLoadResultHandler resultHandler) {
        playerManager.loadItemOrdered(musicManager, token, resultHandler);
    }

    private void spotifyClientCredentials() {

        try {
            ClientCredentialsRequest credentialsRequest = spotifyApi.clientCredentials().build();
            ClientCredentials credentials = credentialsRequest.execute();
            spotifyApi.setAccessToken(credentials.getAccessToken());

            System.out.println("expires in: " + credentials.getExpiresIn());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            //TODO output some form of message in the channel if this fails?
            System.out.println("Error: " + e.getMessage());
        }
    }
}
