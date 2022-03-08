package Core.Music.SpotifyTrackAndPlaylistWrappers;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import se.michaelthelin.spotify.model_objects.specification.Playlist;

import java.util.List;

public class SpotifyPlaylistWrapper implements AudioPlaylist {

    private final Playlist spotifyPlaylist;

    public SpotifyPlaylistWrapper(Playlist spotifyPlaylist) {
        this.spotifyPlaylist = spotifyPlaylist;
    }

    @Override
    public String getName() {
        return spotifyPlaylist.getName();
    }

    @Override
    public List<AudioTrack> getTracks() {
        return null;
    }

    @Override
    public AudioTrack getSelectedTrack() {
        return null;
    }

    @Override
    public boolean isSearchResult() {
        return false;
    }
}
