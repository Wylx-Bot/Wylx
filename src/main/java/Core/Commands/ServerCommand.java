package Core.Commands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ServerCommand {

	public enum CommandPermission {
		EVERYONE,
		DISCORD_PERM,
		ROLE_PERM,
		BOT_ADMIN
	}

	private final CommandPermission cmdPerm;
	private final Permission discPerm;
	private final String keyword;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());;

	public ServerCommand(String keyword, CommandPermission cmdPerm) {
		this.keyword = keyword;
		this.cmdPerm = cmdPerm;
		discPerm = null;

		if (cmdPerm == CommandPermission.DISCORD_PERM) {
			logger.error("Discord Permission Not Specified");
			System.exit(-1);
		}
	}

	public ServerCommand(String keyword, CommandPermission cmdPerm, Permission perm) {
		this.keyword = keyword;
		this.cmdPerm = cmdPerm;
		this.discPerm = perm;

		if (cmdPerm != CommandPermission.DISCORD_PERM) {
			logger.error("Discord permission given when command permission != DISCORD_PERM");
			System.exit(-1);
		}
		if (perm == null) {
			logger.error("Discord permission expected but got NULL");
			System.exit(-1);
		}
	}

	public String getKeyword(){
		return keyword;
	}

	private static final long[] botAdmins = {
		326415273827762176L, /* SnakeJ 		*/
		317075202825781248L, /* Jerbilcraft */
		139548522377641984L, /* 1Revenger1  */
	};

	public boolean checkPermission(MessageReceivedEvent event) {
		var member = event.getMember();
		if (member == null) return false;

		switch (this.cmdPerm) {
			case EVERYONE -> {
				return true;
			}
			case DISCORD_PERM -> {
				return member.hasPermission(this.discPerm);
			}
			case ROLE_PERM -> {
				// TODO: NOOP
				return false;
			}
			case BOT_ADMIN -> {
				// TODO: Put in Database
				var memberId = member.getIdLong();

				for (long admin : botAdmins) {
					if (admin == memberId) return true;
				}

				return false;
			}
		}

		return false;
	}

	abstract public void runCommand(MessageReceivedEvent event, String[] args);

	public String getName(){
		return this.getClass().getSimpleName();
	}
}
