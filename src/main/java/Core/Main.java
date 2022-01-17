package Core;

import Core.Processing.MessageProcessing;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	private static JDA jda;
	private static String token = "";
	static {
		try {
			Scanner scan = new Scanner(new File("src/main/resources/token.txt"));
			token = scan.nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private static final MessageProcessing messageProcessor = new MessageProcessing();

	public static void main(String[] args) throws LoginException, InterruptedException {
		jda = JDABuilder.createDefault(token)
				.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "with half a ship"))
				.addEventListeners(messageProcessor)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.build();
		jda.awaitReady();
	}
}
