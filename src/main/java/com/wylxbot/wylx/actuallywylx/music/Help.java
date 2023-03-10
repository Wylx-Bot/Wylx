package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.actuallywylx.CommandGroup;
import com.wylxbot.wylx.actuallywylx.CommandsContainer;
import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Help extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("help", "Weeeee");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        CommandGroup[] groups = CommandsContainer.Commands;

        StringBuilder builder = new StringBuilder();
        var select = StringSelectMenu.create("help");
        select.addOption("About", "about");
        select.setPlaceholder("Select Help Page here");

        for (int i = 0; i < groups.length; i++) {
            CommandGroup g = groups[i];
            builder.append(g.name()).append(" - ").append(g.desc()).append(" (Definitely Disabled)\n");
            select.addOption(g.name(), Integer.toString(i), g.desc());
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Wylx")
                .addField("About Wylx", "About this stuff, idk. Link github maybe?", false)
                .addField("Command Modules", builder.toString(), false)
                .setColor(Color.PINK)
                .build();

        interaction.replyEmbeds(embed).addActionRow(select.build()).queue();
    }

    @Override
    public void stringMenu(StringSelectInteractionEvent event) {
        CommandGroup[] groups = CommandsContainer.Commands;

        if (event.getValues().get(0).equals("about")) {
            StringBuilder builder = new StringBuilder();

            for (CommandGroup g : groups) {
                builder.append(g.name()).append(" - ").append(g.desc()).append(" (Definitely Disabled)\n");
            }

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Wylx")
                    .addField("About Wylx", "About this stuff, idk. Link github maybe?", false)
                    .addField("Command Modules", builder.toString(), false)
                    .setColor(Color.PINK)
                    .build();

            event.editMessageEmbeds(embed).queue();
            return;
        }

        int groupIdx = Integer.parseInt(event.getValues().get(0));
        CommandGroup group = groups[groupIdx];

        StringBuilder commands = new StringBuilder();
        for (WylxCommand command : group.commands()) {
            SlashCommandData data = command.getSlashCommand();
            commands.append(data.getName()).append(" - ").append(data.getDescription()).append("\n");
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Wylx")
                .addField(groups[groupIdx].name() + " commands", commands.toString(), false)
                .setColor(Color.PINK)
                .build();
        event.editMessageEmbeds(embed).queue();
    }
}
