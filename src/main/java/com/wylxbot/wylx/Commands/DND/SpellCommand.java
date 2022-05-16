package com.wylxbot.wylx.Commands.DND;

import com.wylxbot.wylx.Commands.DND.Util.SpellBuilder;
import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ThreadedCommand;
import com.wylxbot.wylx.Core.Util.Helper;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Consumer;

public class SpellCommand extends ThreadedCommand {
    private SpellBuilder spellBuilder = null;

    public SpellCommand() {
        super("spell", CommandPermission.EVERYONE, """
                Search for a dnd spell and display information on the spell
                Usage: %{p}spell <spell_name>
                """);
    }

    @Override
    public void runCommandThread(CommandContext ctx) {
        // Provide command description if there was no spell name provided
        if(ctx.args().length < 2){
            ctx.event().getMessage().reply(getDescription(ctx.prefix())).queue();
            return;
        }

        if(spellBuilder == null){
            spellBuilder = SpellBuilder.getInstance();
        }

        // Find the closest spell and display its information
        String spellName = String.join(" ", Arrays.copyOfRange(ctx.args(), 1, ctx.args().length));
        String chosenSpellName = spellBuilder.getClosestSpellName(spellName);
        // If spell is null the max errors were exceeded and the user needs to make a choice
        if(chosenSpellName != null){
            sendSpellInfo(chosenSpellName, ctx.event().getChannel());
        } else {
            String[] spellChoices = spellBuilder.getCloseSpellNames(spellName);
            String messageTxt = "Please choose one of the spells below\n";
            for(int i = 0; i < spellChoices.length; i++){
                messageTxt += (i+1) + ": ";
                messageTxt += spellBuilder.capitalizeSpellName(spellChoices[i]) + "\n";
            }

            ctx.event().getChannel().sendMessage(messageTxt).queue(msg -> {
                Helper.chooseFromListWithReactions(msg, ctx.event().getAuthor(),
                        reactionEvent -> { sendSpellInfo(spellChoices[0], ctx.event().getChannel()); },
                        reactionEvent -> { sendSpellInfo(spellChoices[1], ctx.event().getChannel()); },
                        reactionEvent -> { sendSpellInfo(spellChoices[2], ctx.event().getChannel()); },
                        reactionEvent -> { sendSpellInfo(spellChoices[3], ctx.event().getChannel()); },
                        reactionEvent -> { sendSpellInfo(spellChoices[4], ctx.event().getChannel()); });
            });
        }
    }

    private void sendSpellInfo(String spellName, MessageChannel channel){
        channel.sendMessageEmbeds(spellBuilder.getSpell(spellName).getSpellEmbed()).queue();
    }
}
