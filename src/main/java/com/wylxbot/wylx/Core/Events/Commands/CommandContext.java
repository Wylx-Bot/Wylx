package com.wylxbot.wylx.Core.Events.Commands;

import com.wylxbot.wylx.Core.Music.GuildMusicManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record CommandContext(MessageReceivedEvent event,
                             String[] args,
                             String parsedMsg,
                             String prefix,
                             String guildID,
                             String authorID,
                             GuildMusicManager musicManager,
                             DiscordServer db) {
}
