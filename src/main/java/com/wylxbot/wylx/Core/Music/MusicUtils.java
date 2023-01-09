package com.wylxbot.wylx.Core.Music;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Util.ProgressBar;
import com.wylxbot.wylx.Wylx;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.managers.AudioManager;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MusicUtils {

    private static final Pattern argToTimePattern = Pattern.compile("^[+-]?(\\d*:)*\\d+$");

    /**
     * Join a voice channel so music can be played
     *
     * @param ctx TrackContext with details on channel to join
     * @return True if there was an error joining
     */
    public static boolean joinVoice(TrackContext ctx) {
        Wylx wylx = Wylx.getInstance();
        AudioManager audioManager = wylx.getGuildAudioManager(ctx.guildID());
        Member member = wylx.getMemberInGuild(ctx.guildID(), ctx.requesterID());

        // Bot already in voice
        if (audioManager.isConnected())
            return false;

        // Get channel that requester is in to join
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel())
            return true; // User not in voice channel

        audioManager.openAudioConnection(voiceState.getChannel());
        audioManager.setSelfDeafened(true);
        return false;
    }

    public enum VoiceCommandBlockedReason {
        MEMBER_NOT_IN_VOICE("You are not in a voice channel!"),
        MEMBER_IN_DIFF_CHANNEL("You are not in the same channel as the bot!"),
        COMMAND_OK("");

        public final String reason;

        private VoiceCommandBlockedReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * Check that bot is not in any voice channel or that user is in the same channel as bot
     *
     * @param ctx CommandContext which contains member and guild ID
     * @return True if voice commands are allowed
     */
    @SuppressWarnings("ConstantConditions")
    public static VoiceCommandBlockedReason voiceCommandBlocked(CommandContext ctx) {
        Wylx wylx = Wylx.getInstance();
        AudioManager audioManager = wylx.getGuildAudioManager(ctx.guildID());
        Member member = wylx.getMemberInGuild(ctx.guildID(), ctx.authorID());

        // Check user is in a voice channel
        // Note that JDA only caches members in voice channels, so NULL is expected a lot
        if (member == null || !member.getVoiceState().inAudioChannel())
            return VoiceCommandBlockedReason.MEMBER_NOT_IN_VOICE;

        // If bot is not in a voice channel, then it's safe to use commands like "play"
        if (!audioManager.isConnected())
            return VoiceCommandBlockedReason.COMMAND_OK;

        // Check that bot is in the same channel as user
        if (wylx.userInVoiceChannel(ctx.guildID(), audioManager.getConnectedChannel().getIdLong(), ctx.authorID())) {
            return VoiceCommandBlockedReason.COMMAND_OK;
        } else {
            return VoiceCommandBlockedReason.MEMBER_IN_DIFF_CHANNEL;
        }
    }

    /**
     * Nice embed which shows details/thumbnail of given track
     *
     * @param track AudioTrack details
     * @param titleFormat Format for title (Queue or Play)
     * @param progress Show progress through song
     * @return MessageEmbed that can be sent
     */
    public static MessageEmbed createPlayingEmbed(AudioTrack track, String titleFormat, boolean progress) {
        EmbedBuilder builder = new EmbedBuilder();
        AudioTrackInfo info = track.getInfo();
        TrackContext ctx = (TrackContext) track.getUserData();

        builder.setTitle(String.format(titleFormat, info.title), info.uri);
        builder.setDescription(String.format("Uploaded by: %s", info.author));

        if (info.isStream) {
            builder.setFooter("Stream (No Duration)");
        } else {
            String prettyDur = getPrettyDuration(info.length);

            if (ctx.startMillis() != 0) {
                String prettyStart = getPrettyDuration(ctx.startMillis());
                builder.setFooter(String.format("Duration: %s - Started at %s", prettyDur, prettyStart));
            } else {
                builder.setFooter(String.format("Duration: %s", prettyDur));
            }

            if (progress) {
                builder.appendDescription("\n");
                builder.appendDescription(MusicUtils.getPrettyDuration(track.getPosition()));
                builder.appendDescription(" / ");
                builder.appendDescription(prettyDur);
                builder.appendDescription("\n");
                builder.appendDescription(ProgressBar.progressBar((double) track.getPosition() / track.getDuration()));
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
     * @param millis Millisecond duration to prettify
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

        if (dur.isNegative()) {
            str += "-";
            dur = dur.abs();
        }

        if (dur.toHours() != 0) str += String.format("%dh ", dur.toHours());
        if (dur.toMinutesPart() != 0) str += String.format("%dm ", dur.toMinutesPart());
        if (dur.toSecondsPart() != 0) str += String.format("%ds ", dur.toSecondsPart());
        if (dur.toSeconds() == 0) str += "0s";
        return str.trim();
    }

    /**
     * Convert time argument to Duration
     *
     * @param string Seconds or HH:MM:SS or MM:SS string
     * @return Duration
     */
    public static MusicSeek getDurationFromArg(String string) {
        if (!argToTimePattern.matcher(string).matches()) {
            return null;
        }

        boolean relative = false;
        long sign = 1;
        switch (string.charAt(0)) {
            case '+' -> relative = true;
            case '-' -> { relative = true; sign = -1; }
            default -> {}
        }

        List<Integer> parsedArgs = Arrays.stream(string.split(":"))
                .map(Integer::parseInt).toList();

        Duration dur = switch (parsedArgs.size()) {
            case 1 -> Duration.ofSeconds(parsedArgs.get(0));
            case 2 -> Duration.ofMinutes(parsedArgs.get(0))
                    .plus(sign * parsedArgs.get(1), ChronoUnit.SECONDS);
            case 3 -> Duration.ofHours(parsedArgs.get(0))
                    .plus(sign * parsedArgs.get(1), ChronoUnit.MINUTES)
                    .plus(sign * parsedArgs.get(2), ChronoUnit.SECONDS);
            default -> Duration.ofSeconds(0);
        };

        return new MusicSeek(relative, dur);
    }

    /**
     * Get remaining time in playlist
     * Returns null if a stream exists in the playlist or manager is looping the current track
     *
     * @param list Playlist
     * @param manager Music manager for guild
     * @return Remaining time as Duration
     */
    public static Duration getTimeRemaining(Object[] list, GuildMusicManager manager) {
        AudioTrack playingTrack = manager.getCurrentTrack();
        long millis = playingTrack.getDuration() - playingTrack.getPosition();
        if (playingTrack.getInfo().isStream) return null;
        if (manager.isLooping()) return null;

        for (Object track: list) {
            AudioTrack currentTrack = (AudioTrack) track;
            millis += currentTrack.getDuration();
            if (currentTrack.getInfo().isStream) return null;
        }

        return Duration.ofMillis(millis);
    }
}

