package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class RolesSuperCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("roles", "Create and modify role menus!").addSubcommands(
                new SubcommandData("createmenu", "wooot")
                        .addOption(OptionType.CHANNEL, "channel", "woot", true),
                new SubcommandData("addrole", "woot")
                        .addOption(OptionType.ROLE, "role", "role to add", true)
                        .addOption(OptionType.STRING, "menu_id", "role menu", true)
        );
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        switch (interaction.getSubcommandName()) {
            case "createmenu":
                interaction.reply("Creating menu in " +
                        interaction.getOption("channel", OptionMapping::getAsChannel).getAsMention()
                ).queue();
                break;
            default:
                interaction.reply("Bleh").queue();
                break;
        }
    }
}
