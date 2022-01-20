package Core.Music;

import Core.Wylx;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        audioManager.setSelfDeafened(true);
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

    /**
     * Nice embed which shows details/thumbnail of given track
     *
     * @param track AudioTrack details
     * @param titleFormat Format for title (Queue or Play)
     * @return MessageEmbed that can be sent
     */
    public static MessageEmbed createPlayingEmbed(AudioTrack track, String titleFormat) {
        EmbedBuilder builder = new EmbedBuilder();
        AudioTrackInfo info = track.getInfo();
        TrackContext ctx = (TrackContext) track.getUserData();

        builder.setTitle(String.format(titleFormat, info.title), info.uri);
        builder.setDescription(String.format("Uploaded by: %s", info.author));

        if (info.isStream) {
            builder.setFooter("Stream (No Duration)");
        } else {
            String prettyDur = getPrettyDuration(Duration.ofMillis(info.length));

            if (ctx.startMillis != 0) {
                String prettyStart = getPrettyDuration(Duration.ofMillis(ctx.startMillis));
                builder.setFooter(String.format("Duration: %s - Started at %s", prettyDur, prettyStart));
            } else {
                builder.setFooter(String.format("Duration: %s", prettyDur));
            }
        }

        if (info.artworkUrl != null) {
            builder.setImage(info.artworkUrl);
        }

        return builder.build();
    }


    /**
     * Get pretty text duration from milliseconds
     *
     * @param dur Millisecond duration to prettify
     * @return Pretty string
     */
    public static String getPrettyDuration(long millis) {
        return getPrettyDuration(Duration.ofMillis(millis));
    }

    /**
     * Get pretty text duration from Duration
     *
     * @param dur Duration to prettify
     * @return Pretty string
     */
    public static String getPrettyDuration(Duration dur) {
        String str = "";
        if (dur.toHours() > 0) str += String.format("%dh ", dur.toHours());
        if (dur.toMinutesPart() > 0) str += String.format("%dm ", dur.toMinutesPart());
        if (dur.toSecondsPart() > 0) str += String.format("%ds ", dur.toSecondsPart());
        if (dur.toSeconds() == 0) str += "0s";
        return str.trim();
    }

    /**
     * Convert time argument to Duration
     *
     * @param string Seconds or HH:MM:SS or MM:SS string
     * @return Duration
     */
    public static Duration getDurationFromArg(String string) {
        List<Integer> parsedArgs = Arrays.stream(string.split(":"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return switch (parsedArgs.size()) {
            case 1 -> Duration.ofSeconds(parsedArgs.get(0));
            case 2 -> Duration.ofMinutes(parsedArgs.get(0))
                    .plus(parsedArgs.get(1), ChronoUnit.SECONDS);
            case 3 -> Duration.ofHours(parsedArgs.get(0))
                    .plus(parsedArgs.get(1), ChronoUnit.MINUTES)
                    .plus(parsedArgs.get(2), ChronoUnit.SECONDS);
            default -> Duration.ofSeconds(0);
        };
    }

    /**
     * Get remaining time in playlist
     *
     * @param list Playlist
     * @param playingTrack Currently playing track
     * @return Remaining time as Duration
     */
    public static Duration getTimeRemaining(Object[] list, AudioTrack playingTrack) {
        long millis = playingTrack.getDuration() - playingTrack.getPosition();
        if (playingTrack.getInfo().isStream) return null;

        for (Object track: list) {
            var currentTrack = (AudioTrack) track;
            millis += currentTrack.getDuration();
            if (currentTrack.getInfo().isStream) return null;
        }

        return Duration.ofMillis(millis);
    }
}
