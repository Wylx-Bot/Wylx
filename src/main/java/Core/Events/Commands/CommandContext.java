package Core.Events.Commands;

import Core.Music.GuildMusicManager;
import Database.DiscordServer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record CommandContext(MessageReceivedEvent event,
                             String[] args,
                             String parsedMsg,
                             String prefix,
                             String guildID,
                             String authorID,
                             GuildMusicManager musicManager,
                             DiscordServer db) { }
