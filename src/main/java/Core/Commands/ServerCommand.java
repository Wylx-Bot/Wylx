package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ServerCommand {
	//TODO: Get ID of beta server
	private final long betaServerID = 0L;

	private final Permission perm;
	private final boolean beta;
	private final String keyword;

	protected final Logger logger;

	public ServerCommand(String keyword){
		this(keyword, null);
	}

	public ServerCommand(String keyword, Permission perm){
		this(keyword, perm, false);
	}

	public ServerCommand(String keyword, Permission perm, boolean beta){
		this.keyword = keyword;
		this.perm = perm;
		this.beta = beta;

		logger = Logger.getLogger(this.getClass().getSimpleName());
		logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new LogFormatter());
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
	}

	public String getKeyword(){
		return keyword;
	}

	public boolean permissionsCheck(MessageReceivedEvent event){
		return (perm == null || event.getMember().hasPermission(perm)) &&
				(!beta || event.getGuild().getIdLong() == betaServerID);
	}

	abstract public void runCommand(MessageReceivedEvent event);
}
