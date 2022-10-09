package com.wylxbot.wylx.Commands.DND;

import com.wylxbot.wylx.Commands.DND.Util.SpellBuilder;
import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ThreadedCommand;
import com.wylxbot.wylx.Core.Util.Helper;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class SpellCommand extends ThreadedCommand {
    private final static int MAX_ERRORS = 3;
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
        String spellName = String.join(" ", Arrays.copyOfRange(ctx.args(), 1, ctx.args().length)).toLowerCase();
        String[] chosenSpells = spellBuilder.getCloseSpellNames(spellName);
        // See if spell is within acceptable error margins
        if(StringUtils.getLevenshteinDistance(chosenSpells[0], spellName) <= MAX_ERRORS){
            sendSpellInfo(chosenSpells[0], ctx.event().getChannel());
            return;
        }

        // If the spell is outside acceptable error make the user choose
        String[] spellChoices = spellBuilder.getCloseSpellNames(spellName);
        String messageTxt = "Please choose one of the spells below\n";

        for(int i = 0; i < spellChoices.length; i++){
            messageTxt += (i+1) + ": ";
            messageTxt += spellBuilder.capitalizeSpellName(spellChoices[i]) + "\n";
        }

        ctx.event().getChannel().sendMessage(messageTxt).queue(msg -> {
            Helper.chooseFromListWithReactions(msg, ctx.event().getAuthor(), chosenSpells.length,
                    chosenSpellIndex -> sendSpellInfo(chosenSpells[chosenSpellIndex.result()-1], ctx.event().getChannel()), false);
        });
    }

    private void sendSpellInfo(String spellName, MessageChannel channel){
        channel.sendMessageEmbeds(spellBuilder.getSpell(spellName).getSpellEmbed()).queue();
    }
}
