package Core;

import Core.Processing.MessageProcessing;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Main {
	private static final MessageProcessing messageProcessor = new MessageProcessing();

	private static boolean isRelease;

	public static void main(String[] args) throws LoginException, InterruptedException {
		Dotenv env = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		isRelease = Boolean.parseBoolean(env.get("RELEASE"));
		JDA jda = JDABuilder.createDefault(env.get("DISCORD_TOKEN"))
				.setActivity(Activity.of(Activity.ActivityType.PLAYING, "with half a ship"))
				.addEventListeners(messageProcessor)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.build();
		jda.awaitReady();
	}

	public static boolean isRelease(){
		return isRelease;
	}
}
