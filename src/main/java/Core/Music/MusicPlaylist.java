package Core.Music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicPlaylist {
    private final BlockingQueue<AudioTrack> queue;

    public MusicPlaylist() {
        queue = new LinkedBlockingQueue<>();
    }

    public AudioTrack getNextTrack() {
        return queue.size() > 0 ? queue.remove() : null;
    }

    public void queue(AudioTrack newTrack) {
        queue.add(newTrack);
    }

    public void queuePlaylist(List<AudioTrack> tracks) {
        queue.addAll(tracks);
    }

    public void clear() {
        queue.clear();
    }
}
