package Core.Events.Commands;

import Core.Music.GuildMusicManager;
import Database.DbCollection;
import Database.DiscordIdentifiers;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record CommandContext(MessageReceivedEvent event,
                             String[] args,
                             String parsedMsg,
                             String prefix,
                             String guildID,
                             String authorID,
                             GuildMusicManager musicManager,
                             DbCollection<ServerIdentifiers> db) { }
