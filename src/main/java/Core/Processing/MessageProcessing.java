package Core.Processing;

import Commands.BotUtil.BotUtilPackage;
import Commands.DND.TTRPGPackage;
import Commands.Fight.FightPackage;
import Commands.Frog.FrogPackage;
import Commands.Music.MusicPackage;
import Commands.Roles.RolePackage;
import Commands.ServerUtil.ServerUtilPackage;
import Commands.ServerSettings.ServerSettingsPackage;
import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Events.Event;
import Core.Events.EventPackage;
import Core.Events.ServerEventManager;
import Core.Events.SilentEvents.SilentEvent;
import Core.Music.WylxPlayerManager;
import Core.Wylx;
import Core.WylxEnvConfig;
import Database.DatabaseManager;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProcessing extends ListenerAdapter {
    private static final WylxPlayerManager musicPlayerManager = WylxPlayerManager.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessing.class);
    public static final HashMap<String, ServerCommand> commandMap = new HashMap<>();
    public static final ArrayList<SilentEvent> silentEvents = new ArrayList<>();
    public static final HashMap<String, Event> eventMap = new HashMap<>();
    public static final EventPackage[] eventPackages = {
            new ServerUtilPackage(),
            new ServerSettingsPackage(),
            new MusicPackage(),
            new TTRPGPackage(),
            new BotUtilPackage(),
            new FrogPackage(),
            new FightPackage(),
            new RolePackage(),
    };

    static {
        for(EventPackage eventPackage : eventPackages){
            for(ServerCommand command : eventPackage.getCommands()){
                commandMap.putAll(command.getCommandMap());
            }
            silentEvents.addAll(Arrays.asList(eventPackage.getSilentEvents()));
            for(Event event : eventPackage.getEvents()){
                eventMap.putAll(event.getEventMap());
            }
        }
    }

    private final Pattern mentionPattern = Message.MentionType.USER.getPattern();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Wylx wylx = Wylx.getInstance();
        DatabaseManager dbManager = wylx.getDb();

        long memberID = event.getAuthor().getIdLong();
        long guildID = event.getGuild().getIdLong();

        //Ignore messages from the bot
        if(memberID == wylx.getBotID() ||
            !event.getChannel().canTalk() ||
            !event.isFromGuild()) return;

        DiscordServer db = dbManager.getServer(event.getGuild().getId());
        ServerEventManager eventManager = ServerEventManager.getServerEventManager(event.getGuild().getId());
        String serverPrefix = getPrefix(db, wylx);
        String msgStr = event.getMessage().getContentRaw();
        String msgPrefix = null;

        // Check for @Wylx
        Matcher matcher = mentionPattern.matcher(msgStr);

        // Does message start with prefix?
        if (msgStr.startsWith(serverPrefix)) {
            msgPrefix = serverPrefix;

        // Does message start with @Wylx?
        } else if (matcher.find() && matcher.start() == 0 && matcher.group(1).equals(wylx.getBotIDString())) {
            msgPrefix = msgStr.substring(0, matcher.end());
        }

        // Run command if prefix was used
        if (msgPrefix != null) {
            msgStr = msgStr.substring(msgPrefix.length()).trim();
            String[] args = msgStr.split(" ");
            ServerCommand command = commandMap.get(args[0]);

            if (command != null && eventManager.checkEvent(command)) {
                if(command.checkPermission(event)) {
                    CommandContext ctx = new CommandContext(
                        event, args, msgStr, serverPrefix, guildID, memberID,
                        musicPlayerManager.getGuildManager(guildID),
                        db
                    );
                    logger.debug("Command ({}) Called With {} Args", args[0], args.length);
                    command.runCommand(ctx);
                } else {
                    event.getMessage().reply("You don't have permission to use this command!").queue();
                }
                return;
            }
        }

        for(SilentEvent silentEvent : silentEvents){
            if(eventManager.checkEvent(silentEvent) && silentEvent.check(event, serverPrefix)){
                silentEvent.runEvent(event, serverPrefix);
                return;
            }
        }
    }

    private String getPrefix(DiscordServer server, Wylx wylx) {
        WylxEnvConfig config = wylx.getWylxConfig();
        if (config.release) {
            return server.getSetting(ServerIdentifiers.Prefix);
        }

        return config.betaPrefix;
    }
}
