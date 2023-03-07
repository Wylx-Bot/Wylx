package com.wylxbot.wylx;

import com.wylxbot.wylx.Core.Processing.MessageProcessing;
import com.wylxbot.wylx.Core.Processing.ReactionProcessing;
import com.wylxbot.wylx.Core.Processing.VoiceChannelProcessing;
import com.wylxbot.wylx.Core.WylxEnvConfig;
import com.wylxbot.wylx.Database.DatabaseManager;
import com.wylxbot.wylx.actuallywylx.SlashCommandProcessing;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.*;

public class Wylx {
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
    private final WylxEnvConfig wylxConfig;
    private final MessageProcessing msgProc;
    private final Thread shutdownThread = new Thread(this::shutdown, "Shutdown Thread");

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
        msgProc = new MessageProcessing(db);

        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(
                        msgProc,
                        new VoiceChannelProcessing(),
                        new ReactionProcessing()
                ).build();

        SlashCommandProcessing slash = new SlashCommandProcessing(jda);
        jda.addEventListener(slash);

//        jda.updateCommands().queue();
//        jda.updateCommands()
//                .addCommands(Commands.slash("ping", "Get the response time!"))
//                .addCommands(Commands.slash("bonk", "Bonk someone")
//                        .addOption(OptionType.USER, "user", "User to bonk", true))
//                .addCommands(Commands.slash("secret", "Super Secret command"))
//                .addCommands(Commands.slash("roles", "Create and modify role menus!").addSubcommands(
//                        new SubcommandData("createmenu", "wooot")
//                                .addOption(OptionType.CHANNEL, "channel", "woot", true),
//                        new SubcommandData("addrole", "woot")
//                                .addOption(OptionType.ROLE, "role", "role to add", true)
//                                .addOption(OptionType.STRING, "menu_id", "role menu", true)
//                ))
//                .addCommands(Commands.slash("modal", "test modal"))
//                .addCommands(Commands.slash("subgroups", "test command").addSubcommandGroups(
//                        new SubcommandGroupData("group", "group description").addSubcommands(
//                                new SubcommandData("subcommand", "subcommand test")
//                                        .addOption(OptionType.INTEGER, "integer", "woot")
//                        )
//                ))
//                .queue();

        Timer activityTimer = new Timer();
        activityTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setActivity();
            }
        }, 0, ACTIVITY_PERIOD);

        Runtime.getRuntime().addShutdownHook(shutdownThread);
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

    public WylxEnvConfig getWylxConfig() {
        return wylxConfig;
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

    private void shutdown() {
        msgProc.close();
    }
}
