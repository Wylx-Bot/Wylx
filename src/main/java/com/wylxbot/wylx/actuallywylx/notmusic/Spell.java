package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Spell extends WylxCommand {

    private static String[] spells = {
           "Acid Splash",
           "Blade Ward",
           "Booming Blade",
           "Chill Touch",
           "Control Flames",
           "Create Bonfire",
           "Dancing Lights",
           "Druidcraft",
           "Eldritch Blast"
    };

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("spell", "Get spell description!")
                .addOption(OptionType.STRING, "spell", "Spell to get details for", true, true);
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.reply(interaction.getOption("spell", OptionMapping::getAsString)).queue();
    }

    private static Integer LIST_MAX_ERRORS = Integer.MAX_VALUE;

    private record Result(int distance, String name) {};

    @Override
    public void autoComplete(CommandAutoCompleteInteraction autocomplete) {
        PriorityQueue<Result> results = new PriorityQueue<>(1, Comparator.comparingInt((Result a) -> a.distance));

        for(String key : spells){
            int distance = StringUtils.getLevenshteinDistance(key, autocomplete.getFocusedOption().getValue());
            results.add(new Result(distance, key));
        }

        List<Command.Choice> resultList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String name = results.remove().name;
            resultList.add(new Command.Choice(name, name));
        }

        autocomplete.replyChoices(resultList).queue();
    }
}
