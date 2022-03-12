package Core.Processing;

import Commands.BotUtil.BotUtilPackage;
import Commands.DND.TTRPGPackage;
import Commands.Frog.FrogPackage;
import Commands.Help;
import Commands.Music.MusicPackage;
import Commands.ServerUtil.ServerUtilPackage;
import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Events.SilentEvent;
import Core.Music.WylxPlayerManager;
import Core.Wylx;
import Core.ProcessPackage.ProcessPackage;
import Core.WylxEnvConfig;
import Database.DatabaseManager;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MessageProcessing extends ListenerAdapter {
    private static final WylxPlayerManager musicPlayerManager = WylxPlayerManager.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessing.class);
    public static final HashMap<String, ServerCommand> commandMap = new HashMap<>();
    public static final ArrayList<SilentEvent> events = new ArrayList<>();
    public static final ProcessPackage[] processPackages = {
            new ServerUtilPackage(),
            new TTRPGPackage(),
            new MusicPackage(),
            new BotUtilPackage(),
            new FrogPackage()
    };

    static {
        commandMap.put("help", new Help());
        for(ProcessPackage processPackage : processPackages){
            for(ServerCommand command : processPackage.getCommands()){
                commandMap.putAll(command.getCommandMap());
            }
            events.addAll(Arrays.asList(processPackage.getEvents()));
        }
    }

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
        String prefix = getPrefix(db, wylx);
        String msg = event.getMessage().getContentRaw();

        // Check Commands if aimed at bot
        if (msg.startsWith(prefix)) {
            String[] args = msg.split(" ");
            String commandString = args[0].replace(prefix, "").toLowerCase();
            ServerCommand command = commandMap.get(commandString);

            if (command != null) {
                if(command.checkPermission(event)) {
                    CommandContext ctx = new CommandContext(
                        event, args, prefix, guildID, memberID,
                        musicPlayerManager.getGuildManager(guildID),
                        db
                    );
                    logger.debug("Command ({}) Called With {} Args", commandString, args.length);
                    command.runCommand(ctx);
                } else {
                    event.getMessage().reply("You don't have permission to use this command!").queue();
                }
                return;
            }
        }

        for(SilentEvent silentEvent : events){
            if(silentEvent.check(event, prefix)){
                silentEvent.runEvent(event, prefix);
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
