import Commands.Scheduling.EventCoordinator;
import SlashTesting.SlashProcessing;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
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
	private static final EventCoordinator eventCoordinator = new EventCoordinator();
	private static final SlashProcessing slashProcessing = new SlashProcessing();

	public static void main(String[] args) throws LoginException, InterruptedException {
		jda = JDABuilder.createDefault(token)
				.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "with half a ship"))
				.addEventListeners(messageProcessor)
				.addEventListeners(eventCoordinator)
				.addEventListeners(slashProcessing)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.build();
		jda.awaitReady();
		slashProcessing.load(jda);
	}
}
