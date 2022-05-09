package com.wylxbot.wylx.Core.Processing;

import com.wylxbot.wylx.Commands.BotUtil.BotUtilPackage;
import com.wylxbot.wylx.Commands.DND.TTRPGPackage;
import com.wylxbot.wylx.Commands.Fight.FightPackage;
import com.wylxbot.wylx.Commands.Frog.FrogPackage;
import com.wylxbot.wylx.Commands.Music.MusicPackage;
import com.wylxbot.wylx.Commands.Roles.RolePackage;
import com.wylxbot.wylx.Commands.ServerUtil.ServerUtilPackage;
import com.wylxbot.wylx.Commands.ServerSettings.ServerSettingsPackage;
import com.wylxbot.wylx.Commands.TimeConversion.TimePackage;
import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.Event;
import com.wylxbot.wylx.Core.Events.EventPackage;
import com.wylxbot.wylx.Core.Events.ServerEventManager;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;
import com.wylxbot.wylx.Core.Music.WylxPlayerManager;
import com.wylxbot.wylx.Database.DbElements.DiscordGlobal;
import com.wylxbot.wylx.Database.DbElements.GlobalIdentifiers;
import com.wylxbot.wylx.Wylx;
import com.wylxbot.wylx.Core.WylxEnvConfig;
import com.wylxbot.wylx.Database.DatabaseManager;
import com.wylxbot.wylx.Database.DbElements.DiscordServer;
import com.wylxbot.wylx.Database.DbElements.ServerIdentifiers;
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
            new TimePackage(),
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
        long startTime = System.currentTimeMillis();

        Wylx wylx = Wylx.getInstance();
        DatabaseManager dbManager = wylx.getDb();
        String memberID = event.getAuthor().getId();

        //Ignore messages from the bot and in DMs
        if(memberID.equals(wylx.getBotID()) ||
            !event.getChannel().canTalk() ||
            !event.isFromGuild()) {
                return;
        }

        String guildID = event.getGuild().getId();
        DiscordServer db = dbManager.getServer(guildID);
        ServerEventManager eventManager = ServerEventManager.getServerEventManager(guildID);
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
            ServerCommand command = commandMap.get(args[0].toLowerCase());

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
                updateAverageCommand(startTime);
                return;
            }
        }

        for(SilentEvent silentEvent : silentEvents){
            if(eventManager.checkEvent(silentEvent) && silentEvent.check(event, serverPrefix)){
                silentEvent.runEvent(event, serverPrefix);
                updateAverageSilentEvent(startTime);
                return;
            }
        }

        updateAverageNoOp(startTime);
    }

    private void updateAverageCommand(long startTime) {
        DiscordGlobal globalDB = Wylx.getInstance().getDb().getGlobal();

        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Load previous data
        int commandsRun = globalDB.getSetting(GlobalIdentifiers.CommandsProcessed);
        long prevAvg = globalDB.getSetting(GlobalIdentifiers.AverageCommandTime);
        // Calc new avg
        long newAvg = ((prevAvg * commandsRun) / (commandsRun + 1)) + (runTime / (commandsRun + 1));
        // Write new data
        globalDB.setSetting(GlobalIdentifiers.CommandsProcessed, commandsRun + 1);
        globalDB.setSetting(GlobalIdentifiers.AverageCommandTime, newAvg);
    }

    private void updateAverageNoOp(long startTime) {
        DiscordGlobal globalDB = Wylx.getInstance().getDb().getGlobal();

        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Load previous data
        int noOpsRun = globalDB.getSetting(GlobalIdentifiers.NoOpsProcessed);
        long prevAvg = globalDB.getSetting(GlobalIdentifiers.AverageNoOpTime);
        // Calc new avg
        long newAvg = ((prevAvg * noOpsRun) / (noOpsRun + 1)) + (runTime / (noOpsRun + 1));
        // Write new data
        globalDB.setSetting(GlobalIdentifiers.NoOpsProcessed, noOpsRun + 1);
        globalDB.setSetting(GlobalIdentifiers.AverageNoOpTime, newAvg);
    }

    private void updateAverageSilentEvent(long startTime) {
        DiscordGlobal globalDB = Wylx.getInstance().getDb().getGlobal();

        // time from this run
        long runTime = System.currentTimeMillis() - startTime;
        // Load previous data
        int eventsRun = globalDB.getSetting(GlobalIdentifiers.SilentEventsProcessed);
        long prevAvg = globalDB.getSetting(GlobalIdentifiers.AverageSilentEventTime);
        // Calc new avg
        long newAvg = ((prevAvg * eventsRun) / (eventsRun + 1)) + (runTime / (eventsRun + 1));
        // Write new data
        globalDB.setSetting(GlobalIdentifiers.SilentEventsProcessed, eventsRun + 1);
        globalDB.setSetting(GlobalIdentifiers.AverageSilentEventTime, newAvg);
    }

    private String getPrefix(DiscordServer server, Wylx wylx) {
        WylxEnvConfig config = wylx.getWylxConfig();
        if (config.release) {
            return server.getSetting(ServerIdentifiers.Prefix);
        }

        return config.betaPrefix;
    }
}
