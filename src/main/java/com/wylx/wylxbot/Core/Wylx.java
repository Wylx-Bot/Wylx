package com.wylx.wylxbot.Core;

import com.wylx.wylxbot.Core.Processing.MessageProcessing;
import com.wylx.wylxbot.Core.Processing.VoiceChannelProcessing;
import com.wylx.wylxbot.Database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Wylx Bot main class. Connects to Discord and contains some helper methods.
 */
public class Wylx {
    private static final Wylx INSTANCE = new Wylx();
    private static final int ACTIVITY_PERIOD = 60000; // 60 seconds
    private final List<Activity> activities = new ArrayList<>(Arrays.asList(
        Activity.playing("with half a ship"),           // Timelord
        Activity.playing("with other sentient bots"),   // Dragonite
        Activity.playing("with the fate of humanity"),
        Activity.playing("Human Deception Simulator")
    ));

    private JDA jda;
    private final DatabaseManager db;
    private int activityIndex = 0;
    private final WylxEnvConfig wylxConfig;

    public static Wylx getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {}

    private Wylx() {
        String token;
        Dotenv env = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        wylxConfig = new WylxEnvConfig(env);
        if (wylxConfig.release) {
            token = wylxConfig.releaseDiscordToken;
        } else {
            token = wylxConfig.betaDiscordToken;
            activities.add(Activity.playing("with Wylx!"));
        }

        db = new DatabaseManager(wylxConfig);

        try {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(new MessageProcessing(), new VoiceChannelProcessing())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Timer activityTimer = new Timer();
        activityTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setActivity();
            }
        }, 0, ACTIVITY_PERIOD);
    }

    private void setActivity() {
        jda.getPresence().setActivity(activities.get(activityIndex++));
        activityIndex %= activities.size();
    }

    public String getBotId() {
        return jda.getSelfUser().getId();
    }

    public String getBotIdString() {
        return jda.getSelfUser().getId();
    }

    public WylxEnvConfig getWylxConfig() {
        return wylxConfig;
    }

    public DatabaseManager getDb() {
        return db;
    }

    /**
     * Get audio manager from Discord Guild.
     *
     * @param guildId Guild ID
     * @return audio manager
     */
    public AudioManager getGuildAudioManager(String guildId) {
        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            return null;
        }
        return guild.getAudioManager();
    }

    /**
     * Get member from Discord Guild.
     *
     * @param guildId Guild ID
     * @param userId User ID
     * @return Member
     */
    public Member getMemberInGuild(String guildId, String userId) {
        var guild = jda.getGuildById(guildId);
        var user = jda.getUserById(userId);
        if (guild == null || user == null) {
            return null;
        }
        return guild.getMember(user);
    }

    /**
     * Get text or thread channel from channel ID.
     *
     * @param channelId Channel ID
     * @return Message Channel
     */
    public MessageChannel getTextChannel(long channelId) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            return channel;
        } else {
            return jda.getThreadChannelById(channelId);
        }
    }

    /**
     * Check if user is in the voice channel given by channelId.
     *
     * @param guildId Guild ID
     * @param channelId Voice channel ID
     * @param userId User ID
     * @return true if user is in the voice channel channelId
     */
    @SuppressWarnings("ConstantConditions")
    public boolean userInVoiceChannel(String guildId, long channelId, String userId) {
        var member = getMemberInGuild(guildId, userId);
        if (member == null) {
            return false;
        }

        var voiceState = member.getVoiceState();
        return voiceState != null
                && voiceState.inAudioChannel()
                && voiceState.getChannel().getIdLong() == channelId;
    }

    public JDA getJda() {
        return jda;
    }
}
