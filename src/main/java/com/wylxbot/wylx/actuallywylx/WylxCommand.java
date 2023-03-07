package com.wylxbot.wylx.actuallywylx;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class WylxCommand {
    public abstract SlashCommandData getSlashCommand();
    public abstract void doStuff(SlashCommandInteraction interaction);
}
