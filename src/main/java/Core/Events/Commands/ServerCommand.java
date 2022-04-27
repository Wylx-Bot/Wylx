package Core.Events.Commands;
import Core.Events.Event;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Matcher;

public abstract class ServerCommand extends Event {

    public enum CommandPermission {
        EVERYONE,
        DISCORD_PERM,
        ROLE_PERM,
        BOT_ADMIN
    }

    private final CommandPermission cmdPerm;
    private final Permission discPerm;
    private final String keyword;
    private final String description;
    private final String[] aliases;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerCommand(String keyword, CommandPermission cmdPerm, String description){
        this(keyword, cmdPerm, description, new String[]{});
    }

    public ServerCommand(String keyword, CommandPermission cmdPerm, String description, String ... aliases){
        this.keyword = keyword;
        this.cmdPerm = cmdPerm;
        this.description = description;
        this.aliases = aliases;
        discPerm = null;

        if (cmdPerm == CommandPermission.DISCORD_PERM) {
            logger.error("Discord Permission Not Specified");
            System.exit(-1);
        }
    }

    public ServerCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description) {
        this(keyword, cmdPerm, perm, description, new String[]{});
    }

    public ServerCommand(String keyword, CommandPermission cmdPerm, Permission perm, String description, String ... aliases) {
        this.keyword = keyword;
        this.cmdPerm = cmdPerm;
        this.discPerm = perm;
        this.description = description;
        this.aliases = aliases;

        if (cmdPerm != CommandPermission.DISCORD_PERM) {
            logger.error("Discord permission given when command permission != DISCORD_PERM");
            System.exit(-1);
        }
        if (perm == null) {
            logger.error("Discord permission expected but got NULL");
            System.exit(-1);
        }
    }

    @Override
    public String getKeyword(){
        return keyword;
    }

    private static final long[] botAdmins = {
        326415273827762176L, /* SnakeJ		*/
        317075202825781248L, /* Jerbilcraft	*/
        139548522377641984L, /* 1Revenger1	*/
        749499267995140228L, /* BarleyZP	*/
    };

    public boolean checkPermission(MessageReceivedEvent event) {
        Member member = event.getMember();
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
                long memberId = member.getIdLong();

                for (long admin : botAdmins) {
                    if (admin == memberId) return true;
                }

                return false;
            }
        }

        return false;
    }

    @Override
    public final String[] getAliases(){
        return aliases;
    }

    public final HashMap<String, ServerCommand> getCommandMap(){
        HashMap<String, ServerCommand> myMap = new HashMap<>();
        myMap.put(this.keyword, this);
        for(String alias : aliases){
            myMap.put(alias, this);
        }
        return myMap;
    }

    abstract public void runCommand(CommandContext ctx);

    public String getName(){
        return this.getClass().getSimpleName();
    }
    public static String replacePrefix(String string, String prefix) {
        return string.replaceAll("%\\{p}", Matcher.quoteReplacement(prefix));
    }

    @Override
    public String getDescription(String prefix) {
        return replacePrefix(description, prefix);
    }
}
