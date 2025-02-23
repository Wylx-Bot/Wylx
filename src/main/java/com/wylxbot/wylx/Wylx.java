package com.wylxbot.wylx;

import com.wylxbot.wylx.Core.Processing.MessageProcessing;
import com.wylxbot.wylx.Core.Processing.ReactionProcessing;
import com.wylxbot.wylx.Core.Processing.VoiceChannelProcessing;
import com.wylxbot.wylx.Core.WylxEnvConfig;
import com.wylxbot.wylx.Database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Wylx {
    private static final WylxEnvConfig ENV_CONFIG = new WylxEnvConfig(
            Dotenv.configure().ignoreIfMissing().load()
    );
    private static final Wylx INSTANCE = new Wylx();
    private static final int ACTIVITY_PERIOD = 60000; // 60 seconds
    private final List<Activity> activities = new ArrayList<>(Arrays.asList(
        Activity.playing("with half a ship"), 			// Timelord
        Activity.playing("with other sentient bots"), 	// Dragonite
        Activity.playing("with the fate of humanity"),
        Activity.playing("Human Deception Simulator")
    ));

    private JDA jda;
    private DatabaseManager db;
    private int activityIndex = 0;

    public static Wylx getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {}

    private Wylx() {
        String token;

        if (ENV_CONFIG.release) {
            token = ENV_CONFIG.releaseDiscordToken;
        } else {
            token = ENV_CONFIG.betaDiscordToken;
            activities.add(Activity.playing("with Wylx!"));
        }

        db = new DatabaseManager(ENV_CONFIG);

        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(
                        new MessageProcessing(),
                        new VoiceChannelProcessing(),
                        new ReactionProcessing()
                ).build();

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

    public String getBotID(){
        return jda.getSelfUser().getId();
    }

    public String getBotIDString() {
        return jda.getSelfUser().getId();
    }

    public static WylxEnvConfig getWylxConfig() {
        return ENV_CONFIG;
    }

    public DatabaseManager getDb() {
        return db;
    }

    public AudioManager getGuildAudioManager(String guildID) {
        Guild guild = jda.getGuildById(guildID);
        if (guild == null) return null;
        return guild.getAudioManager();
    }

    public Member getMemberInGuild(String guildID, String userID) {
        Guild guild = jda.getGuildById(guildID);
        User user = jda.getUserById(userID);
        if (guild == null || user == null) return null;
        return guild.getMember(user);
    }

    public MessageChannel getTextChannel(long channelID) {
        TextChannel channel = jda.getTextChannelById(channelID);
        if (channel != null) return channel;
        else return jda.getThreadChannelById(channelID);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean userInVoiceChannel(String guildID, long channelID, String userID) {
        Member member = getMemberInGuild(guildID, userID);
        if (member == null) return false;
        GuildVoiceState voiceState = member.getVoiceState();
        return voiceState != null &&
                voiceState.inAudioChannel() &&
                voiceState.getChannel().getIdLong() == channelID;
    }

    public JDA getJDA(){
        return jda;
    }
}
