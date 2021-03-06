package com.wylxbot.wylx.Commands.DND.Util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record Spell(String name, String source, int level, String school, String castingTime, String range, String components, String duration, String description, String[] classSpellLists) {

    public MessageEmbed getSpellEmbed() {
        EmbedBuilder spellEmbed = new EmbedBuilder();
        spellEmbed.setTitle(name);
        spellEmbed.setColor(SpellBuilder.getInstance().getSchoolColor(school));
        // Format the header for the section
        String header = level == 0 ?
                String.format("Cantrip, %s From %s", school, source) :
                String.format("%s Level, %s From %s", getNumberWithSuffix(level), school, source);
        // Format the message into the style we want to present
        String formattedMessage = String.format("""
                **Casting Time:** %s
                **Range:** %s
                **Duration:** %s
                **Components:** %s
                **Classes:** %s
                **Description:** %s
                """,
                castingTime,
                range,
                duration,
                components,
                String.join(", ", classSpellLists),
                description
            );
        // Trim the message so it doesn't exceed the max allowable length for embeds
        if(formattedMessage.length() > 1024){
            formattedMessage = formattedMessage.substring(0, 1021) + "...";
        }

        spellEmbed.addField(header, formattedMessage, false);

        return spellEmbed.build();
    }

    private String getNumberWithSuffix(int number){
        return switch(number){
            case 1 -> number + "st";
            case 2 -> number + "nd";
            case 3 -> number + "rd";
            default -> number + "th";
        };
    }
}
