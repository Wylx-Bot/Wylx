package com.wylxbot.wylx.actuallywylx.notmusic;

import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class ModalCommand extends WylxCommand {
    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("modal", "test modal");
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
                .setPlaceholder("Subject of this ticket")
                .setMinLength(10)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();

        TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your concerns go here")
                .setMinLength(30)
                .setMaxLength(1000)
                .build();

        interaction.replyModal(Modal.create("123", "Title")
                .addActionRow(subject).addActionRow(body).build()).queue();
    }
}
