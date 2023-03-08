package com.wylxbot.wylx.actuallywylx;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class WylxCommand {
    public abstract SlashCommandData getSlashCommand();
    public abstract void doStuff(SlashCommandInteraction interaction);
    public void autoComplete(CommandAutoCompleteInteraction autocomplete) {}
    public void stringMenu(StringSelectInteractionEvent event) {}
}
