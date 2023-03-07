package com.wylxbot.wylx.actuallywylx;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SlashCommandProcessing extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SlashCommandProcessing.class);

    private final Map<String, WylxCommand> commandMap = new HashMap<>();

    private final ThreadPoolExecutor threadPool;

    public SlashCommandProcessing(JDA jda) {
        threadPool = new ThreadPoolExecutor(
                2,
                Integer.MAX_VALUE,
                2,
                TimeUnit.MINUTES,
                new SynchronousQueue<>()
        );

        List<SlashCommandData> commands = new ArrayList<>();

        for (CommandGroup group : CommandsContainer.Commands) {
            for (WylxCommand command : group.commands()) {
                SlashCommandData data = command.getSlashCommand();
                commands.add(data);
                commandMap.put(data.getName(), command);
            }
        }

        jda.updateCommands().addCommands(commands).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        threadPool.execute(() -> {
            WylxCommand command = commandMap.get(event.getName());

            if (command == null) {
                event.reply("Wtf!?!?").queue();
                return;
            }

            command.doStuff(event);
        });
//        if (event.getName().equals("secret")) {
//            event.reply("You have acquired the secret!").setEphemeral(true).queue();
//        } else if (event.getName().equals("bonk")) {
//            event.reply(event.getOption("user", OptionMapping::getAsUser).getAsMention() + " has been bonked").queue();
//        } else if (event.getName().equals("modal")) {
//            TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
//                    .setPlaceholder("Subject of this ticket")
//                    .setMinLength(10)
//                    .setMaxLength(100) // or setRequiredRange(10, 100)
//                    .build();
//
//            TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
//                    .setPlaceholder("Your concerns go here")
//                    .setMinLength(30)
//                    .setMaxLength(1000)
//                    .build();
//            event.replyModal(Modal.create("123", "Title")
//                    .addActionRow(subject).addActionRow(body).build()).queue();
//        } else if (event.getName().equals("roles") && event.getSubcommandName().equals("createmenu")) {
//            event.reply("Creating menu in " + event.getOption("channel", OptionMapping::getAsChannel).getAsMention()).queue();
//        } else {
//                event.reply(event.getName()).queue();
//        }

    }
}
