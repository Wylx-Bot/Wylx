package com.wylxbot.wylx.Commands.DND;

import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import net.dv8tion.jda.api.utils.FileUpload;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LaTexCommand extends ServerCommand {
    public LaTexCommand() {
        super("latex", CommandPermission.EVERYONE, """
                Generates an image based on the provided LaTex formula string
                Usage: %{p}latex <formula>
                """, "tex");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        String latex = ctx.parsedMsg().substring(ctx.parsedMsg().indexOf(' ') + 1);
        TeXFormula formula;

        // Catch any error in the user input and return that to the user
        try {
            formula = new TeXFormula(latex);
        } catch (org.scilab.forge.jlatexmath.ParseException parseException) {
            ctx.event().getChannel().sendMessage(parseException.getMessage()).queue();
            return;
        }

        // Turn export into something we can send to the user
        BufferedImage export = (BufferedImage) formula.createBufferedImage(TeXConstants.STYLE_DISPLAY, 25, Color.white, new Color(49, 51, 56));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // This error should never occur as we are writing to memory and not disk
        try {
            ImageIO.write(export, "png", baos);
        } catch (IOException e) {
            ctx.event().getChannel().sendMessage("Unable to complete request").queue();
            return;
        }

        // Send output to the user
        ctx.event().getChannel().sendFiles(FileUpload.fromData(baos.toByteArray(), "latex.png")).queue();
    }
}
