package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LaTexCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("tex", "Generates an image based on the provided LaTex formula string")
                .addOption(OptionType.STRING, "formula", "LaTeX String", true);
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.deferReply().queue();
        String latex = interaction.getOption("formula", OptionMapping::getAsString);
        TeXFormula formula;

        // Catch any error in the user input and return that to the user
        try {
            formula = new TeXFormula(latex);
        } catch (org.scilab.forge.jlatexmath.ParseException parseException) {
            interaction.getHook().sendMessage(parseException.getMessage()).queue();
            return;
        }

        // Turn export into something we can send to the user
        BufferedImage export = (BufferedImage) formula.createBufferedImage(TeXConstants.STYLE_DISPLAY, 25, Color.white, new Color(49, 51, 56));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // This error should never occur as we are writing to memory and not disk
        try {
            ImageIO.write(export, "png", baos);
        } catch (IOException e) {
            interaction.getHook().sendMessage("Unable to complete request").queue();
            return;
        }

        // Send output to the user
        interaction.getHook().sendFiles(FileUpload.fromData(baos.toByteArray(), "latex.png")).queue();
    }
}
