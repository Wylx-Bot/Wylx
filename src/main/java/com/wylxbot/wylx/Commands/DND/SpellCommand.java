package com.wylxbot.wylx.Commands.DND;

import com.wylxbot.wylx.Commands.DND.Util.Spell;
import com.wylxbot.wylx.Commands.DND.Util.SpellBuilder;
import com.wylxbot.wylx.Core.Events.Commands.CommandContext;
import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.Commands.ThreadedCommand;

import java.util.Arrays;

public class SpellCommand extends ThreadedCommand {
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

        // Find the closest spell and display its information
        String spellName = String.join(" ", Arrays.copyOfRange(ctx.args(), 1, ctx.args().length));
        Spell spell = SpellBuilder.getInstance().getSpell(spellName);
        ctx.event().getChannel().sendMessageEmbeds(spell.getSpellEmbed()).queue();
    }
}
