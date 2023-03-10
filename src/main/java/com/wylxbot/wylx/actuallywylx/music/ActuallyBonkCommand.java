package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ActuallyBonkCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("bonk", "Weeeee")
                .addOption(OptionType.USER, "user", "Person to bonk", true);
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.reply(interaction.getOption(
                "user",
                OptionMapping::getAsUser
        ).getAsMention() + " has been bonked").queue();
    }
}
