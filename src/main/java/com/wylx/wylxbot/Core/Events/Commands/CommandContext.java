package com.wylx.wylxbot.Core.Events.Commands;

import com.wylx.wylxbot.Core.Music.GuildMusicManager;
import com.wylx.wylxbot.Database.DiscordServer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public record CommandContext(MessageReceivedEvent event,
                             String[] args,
                             String parsedMsg,
                             String prefix,
                             String guildID,
                             String authorID,
                             GuildMusicManager musicManager,
                             DiscordServer db) { }
