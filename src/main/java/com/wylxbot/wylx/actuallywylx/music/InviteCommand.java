package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class InviteCommand extends WylxCommand {
    private final static String URL = "<https://www.youtube.com/watch?v=dQw4w9WgXcQ>";

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("invite", "Get a link to add the bot to your server");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.reply("Click here to not invite me to your server: " + URL).queue();
    }
}
