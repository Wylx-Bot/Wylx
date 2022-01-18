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

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class GuildMusicManager extends AudioEventAdapter {
    private Timer discTimer;
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

        if (joinVoice((TrackContext) tracks.get(0).getUserData())) return;
        if (discTimer != null) discTimer.cancel();
        discTimer = null;
        if (player.startTrack(tracks.get(0), true)) {
            tracks.remove(0);
        }

        playlist.queuePlaylist(tracks);
    }

    public void queue(AudioTrack newTrack) {
        if (joinVoice((TrackContext) newTrack.getUserData())) return;
        if (discTimer != null) discTimer.cancel();
        discTimer = null;
        if (player.startTrack(newTrack, true))
            return;

        playlist.queue(newTrack);
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

    private boolean joinVoice(TrackContext ctx) {
        var wylx = Wylx.getInstance();
        var audioManager = wylx.getGuildAudioManager(ctx.guildID);
        var member = wylx.getMemberInGuild(ctx.guildID, ctx.requesterID);

        // Bot already in voice
        if (audioManager.isConnected())
            return false;

        // Get channel that requester is in to join
        var voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel())
            return true; // User not in voice channel

        audioManager.openAudioConnection(voiceState.getChannel());
        return false;
    }

    private void playNextTrack() {
        if (player.startTrack(playlist.getNextTrack(), false))
            return;

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
        TrackContext ctx = (TrackContext) track.getUserData();
        var channel = wylx.getTextChannel(ctx.channelID);

        if (channel == null) return;
        channel.sendMessage("Playing **" + track.getInfo().title + "**")
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
