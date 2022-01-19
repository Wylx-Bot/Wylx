package Core.Music;

import Core.Wylx;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static Core.Music.MusicUtils.joinVoice;

public class GuildMusicManager extends AudioEventAdapter {
    private Timer discTimer;
    private TrackContext lastCtx;
    private final long guildID;
    private final AudioPlayer player;
    private final MusicPlaylist playlist = new MusicPlaylist();

    public GuildMusicManager(long guildID, AudioPlayerManager manager) {
        this.guildID = guildID;
        player = manager.createPlayer();
        player.addListener(this);
        player.setVolume(20);

        var audioManager = Wylx.getInstance().getGuildAudioManager(guildID);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
    }

    public void queuePlaylist(AudioPlaylist newPlaylist) {
        var tracks = newPlaylist.getTracks();
        var ctx = (TrackContext) tracks.get(0).getUserData();
        var textChannel = Wylx.getInstance().getTextChannel(ctx.channelID);

        cancelTimer();
        if (joinVoice(ctx)) {
            return;
        }

        textChannel.sendMessage(String.format("Playlist found: Added %d Songs", tracks.size()))
                .delay(Duration.ofSeconds(60))
                .flatMap(Message::delete)
                .queue();

        if (player.startTrack(tracks.get(0), true)) {
            tracks.remove(0);
        }

        playlist.queuePlaylist(tracks);
    }

    public void queue(AudioTrack newTrack) {
        var ctx = (TrackContext) newTrack.getUserData();
        var textChannel = Wylx.getInstance().getTextChannel(ctx.channelID);

        cancelTimer();
        if (joinVoice(ctx)) {
            return;
        }

        if (player.startTrack(newTrack, true))
            return;

        // Only send if queued instead of played right away
        MessageEmbed embed = MusicUtils.createPlayingEmbed(newTrack, "Queueing **%s**");
        textChannel.sendMessageEmbeds(embed)
                .delay(Duration.ofSeconds(60))
                .flatMap(Message::delete)
                .queue();

        playlist.queue(newTrack);
    }

    private void cancelTimer() {
        if (discTimer != null) {
            discTimer.cancel();
            discTimer = null;
        }
    }

    public void skip() {
        // TODO: Announce skip

        playNextTrack();
    }

    public void clearQueue() {
        playlist.clear();
        playNextTrack();
    }

    public void setVolume(int vol) {
        player.setVolume(vol);
    }

    private void playNextTrack() {
        if (player.startTrack(playlist.getNextTrack(), false))
            return;

        if (lastCtx != null) {
            TextChannel channel = Wylx.getInstance().getTextChannel(lastCtx.channelID);
            channel.sendMessage("Playlist ended. Use the $play command to add more music")
                    .delay(Duration.ofMinutes(1))
                    .flatMap(Message::delete)
                    .queue();
        }

        // Disconnect after a minute if not playing
        discTimer = new Timer();
        discTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                var audioManager = Wylx.getInstance().getGuildAudioManager(guildID);
                audioManager.closeAudioConnection();
            }
        }, 60000);
    }

    public boolean isNotPlaying() {
        return player.getPlayingTrack() == null;
    }

    public void pause(boolean pause) {
        player.setPaused(pause);
    }

    public Object[] getQueue() {
        return playlist.getQueue();
    }

    public AudioTrack getCurrentTrack() {
        return player.getPlayingTrack();
    }

    public void seek(Duration dur) {
        player.getPlayingTrack().setPosition(dur.toMillis());
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        var wylx = Wylx.getInstance();
        lastCtx = (TrackContext) track.getUserData();
        var channel = wylx.getTextChannel(lastCtx.channelID);

        track.setPosition(lastCtx.startMillis);

        if (channel == null) return;
        MessageEmbed embed = MusicUtils.createPlayingEmbed(track, "Playing **%s**");
        channel.sendMessageEmbeds(embed)
                .delay(Duration.ofSeconds(60))
                .flatMap(Message::delete)
                .queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            playNextTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
    }
}
