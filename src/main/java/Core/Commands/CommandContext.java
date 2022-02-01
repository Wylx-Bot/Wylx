package Core.Commands;

import Core.Music.GuildMusicManager;

public record CommandContext(String[] args,
                             String prefix,
                             long guildID,
                             long authorID,
                             GuildMusicManager musicManager) { }
