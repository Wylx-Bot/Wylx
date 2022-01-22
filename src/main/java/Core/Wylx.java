package Core;

import Core.Processing.MessageProcessing;
import Core.Processing.VoiceChannelProcessing;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Wylx {
	private static final Wylx INSTANCE = new Wylx();

	private JDA jda;

	private final boolean isRelease;

	public static Wylx getInstance() {
		return INSTANCE;
	}

	public static void main(String[] args) {}

	private Wylx() {
		Dotenv env = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		isRelease = Boolean.parseBoolean(env.get("RELEASE"));

		try {
			jda = JDABuilder.createDefault(env.get("DISCORD_TOKEN"))
					.setActivity(Activity.of(Activity.ActivityType.PLAYING, "with half a ship"))
					.addEventListeners(new MessageProcessing(), new VoiceChannelProcessing())
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.build();
		} catch (LoginException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public boolean isRelease(){
		return isRelease;
	}

	public long getBotID(){
		return jda.getSelfUser().getIdLong();
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
}
