package Core.Music;

import Core.Wylx;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Duration;

public class MusicUtils {

    /**
     * Join a voice channel so music can be played
     *
     * @param ctx TrackContext with details on channel to join
     * @return True if there was an error joining
     */
    public static boolean joinVoice(TrackContext ctx) {
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

    /**
     * Check that bot is not in any voice channel or that user is in the same channel as bot
     *
     * @param guildID guild ID
     * @param requesterID member ID
     * @return True if voice commands are allowed
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean canUseVoiceCommand(long guildID, long requesterID) {
        var wylx = Wylx.getInstance();
        var audioManager = wylx.getGuildAudioManager(guildID);

        if (!audioManager.isConnected()) return true;
        return wylx.userInVoiceChannel(guildID,
                audioManager.getConnectedChannel().getIdLong(),
                requesterID);
    }

    public static MessageEmbed createPlayingEmbed(AudioTrackInfo info) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(String.format("Playing **%s**", info.title), info.uri);
        builder.setDescription(String.format("Uploaded by: %s", info.author));

        if (info.isStream) {
            builder.setFooter("Stream (No Duration)");
        } else {
            String prettyDur = getPrettyDuration(Duration.ofMillis(info.length));
            builder.setFooter(String.format("Duration: %s", prettyDur));
        }

        if (info.artworkUrl != null) {
            builder.setImage(info.artworkUrl);
        }

        return builder.build();
    }

    public static String getPrettyDuration(long millis) {
        return getPrettyDuration(Duration.ofMillis(millis));
    }
    public static String getPrettyDuration(Duration dur) {
        String str = "";
        if (dur.toHours() > 0) str += String.format("%dh ", dur.toHours());
        if (dur.toMinutes() > 0) str += String.format("%dm ", dur.toMinutesPart());
        if (dur.toSeconds() > 0) str += String.format("%ds ", dur.toSecondsPart());
        return str.trim();
    }


    public static long getTimeRemaining(Object[] list, AudioTrack playingTrack) {
        long millis = playingTrack.getDuration() - playingTrack.getPosition();
        if (playingTrack.getInfo().isStream) return -1;

        for (Object track: list) {
            var currentTrack = (AudioTrack) track;
            millis += currentTrack.getDuration();
            if (currentTrack.getInfo().isStream) return -1;
        }

        return millis;
    }
}
