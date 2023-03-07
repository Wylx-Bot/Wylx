package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class WaitComand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("wait", "Create music with pings!");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.deferReply().queue();
        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
            interaction.getHook().sendMessage("REEEE").queue();
        } finally {
            interaction.getHook().sendMessage("Done!").queue();
        }
    }
}
