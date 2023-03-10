package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.collections4.TrieUtils;
import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.*;
import java.util.List;

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

    PatriciaTrie<String> trie = new PatriciaTrie<>();

    public Spell() {
        for (String s : spells) {
            trie.put(s.toLowerCase(), s);
        }
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("spell", "Get spell description!")
                .addOption(OptionType.STRING, "spell", "Spell to get details for", true, true);
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        interaction.reply(interaction.getOption("spell", OptionMapping::getAsString)).queue();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteraction autocomplete) {
        List<Command.Choice> resultList = new ArrayList<>();
        var res = trie.prefixMap(autocomplete.getFocusedOption().getValue().toLowerCase());

        for (String entry : res.values()) {
            resultList.add(new Command.Choice(entry, entry));
            if (resultList.size() >= 25) break;
        }

        autocomplete.replyChoices(resultList).queue();
    }
}
