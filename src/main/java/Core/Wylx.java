package Core;

import Core.Processing.MessageProcessing;
import Core.Processing.VoiceChannelProcessing;
import Database.DatabaseManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.security.auth.login.LoginException;
import java.util.*;

public class Wylx {
    private static final Wylx INSTANCE = new Wylx();
    private static final int ACTIVITY_PERIOD = 60000; // 60 seconds
    private final List<Activity> activities = new ArrayList<>(Arrays.asList(
        Activity.playing("with youw heawt uwu"), 			// Timelord
        Activity.playing("on uwu street"), 	// Dragonite
        Activity.playing("with cat girls")
    ));

    private JDA jda;
    private DatabaseManager db;
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

    public long getBotID(){
        return jda.getSelfUser().getIdLong();
    }

    public String getBotIDString() {
        return jda.getSelfUser().getId();
    }

    public WylxEnvConfig getWylxConfig() {
        return wylxConfig;
    }

    public DatabaseManager getDb() {
        return db;
    }

    public AudioManager getGuildAudioManager(long guildID) {
        var guild = jda.getGuildById(guildID);
        if (guild == null) return null;
        return guild.getAudioManager();
    }

    public Member getMemberInGuild(long guildID, long userID) {
        var guild = jda.getGuildById(guildID);
        var user = jda.getUserById(userID);
        if (guild == null || user == null) return null;
        return guild.getMember(user);
    }

    public MessageChannel getTextChannel(long channelID) {
        TextChannel channel = jda.getTextChannelById(channelID);
        if (channel != null) return channel;
        else return jda.getThreadChannelById(channelID);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean userInVoiceChannel(long guildID, long channelID, long userID) {
        var member = getMemberInGuild(guildID, userID);
        if (member == null) return false;
        var voiceState = member.getVoiceState();
        return voiceState != null &&
                voiceState.inAudioChannel() &&
                voiceState.getChannel().getIdLong() == channelID;
    }

    public JDA getJDA(){
        return jda;
    }
}
