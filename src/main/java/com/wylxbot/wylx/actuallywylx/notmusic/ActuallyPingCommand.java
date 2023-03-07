package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ActuallyPingCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("ping", "Create music with pings!");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        JDA jda = interaction.getJDA();
        interaction.deferReply().queue();
        jda.getRestPing().queue(time -> {
            String msg = String.format("Pong! REST response time was %d ms. Websocket ping is %s ms\n",
                    time, jda.getGatewayPing());
            interaction.getHook().sendMessage(msg).queue();
        });
    }
}
