package com.wylx.wylxbot.Core.Processing;

import com.wylx.wylxbot.Commands.BotUtil.BotUtilPackage;
import com.wylx.wylxbot.Commands.DND.TTRPGPackage;
import com.wylx.wylxbot.Commands.Fight.FightPackage;
import com.wylx.wylxbot.Commands.Frog.FrogPackage;
import com.wylx.wylxbot.Commands.Music.MusicPackage;
import com.wylx.wylxbot.Commands.Roles.RolePackage;
import com.wylx.wylxbot.Commands.ServerUtil.ServerUtilPackage;
import com.wylx.wylxbot.Commands.ServerSettings.ServerSettingsPackage;
import com.wylx.wylxbot.Commands.TimeConversion.TimePackage;
import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import com.wylx.wylxbot.Core.Events.Event;
import com.wylx.wylxbot.Core.Events.EventPackage;
import com.wylx.wylxbot.Core.Events.ServerEventManager;
import com.wylx.wylxbot.Core.Events.SilentEvents.SilentEvent;
import com.wylx.wylxbot.Core.Music.WylxPlayerManager;
import com.wylx.wylxbot.Core.Wylx;
import com.wylx.wylxbot.Core.WylxEnvConfig;
import com.wylx.wylxbot.Database.DatabaseManager;
import com.wylx.wylxbot.Database.DiscordServer;
import com.wylx.wylxbot.Database.ServerIdentifiers;
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
        Wylx wylx = Wylx.getInstance();
        DatabaseManager dbManager = wylx.getDb();

        String memberID = event.getAuthor().getId();
        String guildID = event.getGuild().getId();

        //Ignore messages from the bot
        if(memberID.equals(wylx.getBotID()) ||
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
