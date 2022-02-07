package Core.Commands;

import Core.Music.GuildMusicManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record CommandContext(MessageReceivedEvent event,
							 String[] args,
							 String prefix,
							 long guildID,
							 long authorID,
							 GuildMusicManager musicManager) { }
