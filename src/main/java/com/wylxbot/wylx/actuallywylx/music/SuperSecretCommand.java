package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SuperSecretCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("secret", "Super Secret command");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.reply("You have acquired the secret!").setEphemeral(true).queue();
    }
}
