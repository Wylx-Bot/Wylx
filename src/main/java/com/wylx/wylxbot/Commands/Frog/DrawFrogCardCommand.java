package com.wylx.wylxbot.Commands.Frog;

import com.wylx.wylxbot.Core.Events.Commands.CommandContext;
import com.wylx.wylxbot.Core.Events.Commands.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class DrawFrogCardCommand extends ServerCommand {

    public DrawFrogCardCommand() {
        super("drawfrogcard", CommandPermission.EVERYONE, "Draws a frog card. (Functions like a standard deck of 52 cards)");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        String suit = "";
        String face;

        EmbedBuilder embed = new EmbedBuilder();

        Random r = new Random();
        switch (r.nextInt(4)) {
            case 0 -> {
                suit = "tree frogs";
                embed.setImage("https://www.rainforest-alliance.org/wp-content/uploads/2021/06/red-eyed-tree-frog-square-1.jpg.optimal.jpg");
            }
            case 1 -> {
                suit = "bullfrogs";
                embed.setImage("https://res.cloudinary.com/fleetnation/image/private/c_fill,g_center,h_640,w_640/v1549761453/t7nbwkgqgghth1tc97cu.jpg");
            }
            case 2 -> {
                suit = "toads";
                embed.setImage("https://lafeber.com/vet/wp-content/uploads/bullfrog-cropped-square-width-500.jpg");
            }
            case 3 -> {
                suit = "poison dart frogs";
                embed.setImage("https://www.rainforest-alliance.org/wp-content/uploads/2021/06/poison-dart-frog-thumb-1-scaled.jpg.optimal.jpg");
            }
        }

        int num = r.nextInt(13) + 1;
        face = switch (num) {
            case 1 -> "Ace";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> String.valueOf(num);
        };

        embed.setTitle("Drew the " + face + " of " + suit);
        embed.setColor(new Color(107, 179, 130));
        embed.setFooter("Frog believes in you.");
        ctx.event().getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
