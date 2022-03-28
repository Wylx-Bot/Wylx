package Commands.DND;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SpellCommand extends ServerCommand {
	// Spells are stored at their name (lowercase)
	// Value is a string array (0: name, 1: source, 2: level, 3: school, 4: casting time, 5: range, 6: components, 7: duration, 8: description, 9: spell lists)
	// Spell Map is null until it has been loaded from disk
	private static HashMap<String, String[]> spellMap = null;

	public SpellCommand() {
		super("dndspell", CommandPermission.EVERYONE, """
				%{p}dndspell <spell_name> provides information on the requested spell
				%{p}dndspell provides a list of all spells in the database
				""", "spell");
	}

	@Override
	public void runCommand(CommandContext ctx) {
		if(ctx.args().length < 2){
			ctx.event().getMessage().reply(getDescription(ctx.prefix())).queue();
		} else {
			spellSearch(ctx);
		}
	}

	private void spellSearch(CommandContext ctx){
		// Load spells, error on fail
		if(!loadSpells()) return;
		String spellName = ctx.event().getMessage().getContentRaw().toLowerCase().substring(ctx.args()[0].length() + 1);

		// Check to see if the spell exists
		if(!spellMap.containsKey(spellName)){
			ctx.event().getMessage().reply("\"" + spellName + "\" not found").queue();
			return;
		}

		// Build embedded to display spell information
		// (0: name, 1: source, 2: level, 3: school, 4: casting time, 5: range, 6: components, 7: duration, 8: description, 9: spell lists)
		String[] spellInfo = spellMap.get(spellName);
		EmbedBuilder spellEmbed = new EmbedBuilder();

		spellEmbed.setTitle(spellInfo[0]);
		spellEmbed.setColor(colorBySchool(spellInfo[3]));
		System.out.println(spellInfo[3]);

		ctx.event().getMessage().getChannel().sendMessageEmbeds(spellEmbed.build()).queue();
	}

	private Color colorBySchool(String school){
		return switch (school) {
			case "abjuration" -> Color.WHITE;
			case "transmutation" -> Color.GREEN;
			case "conjuration" -> Color.YELLOW;
			case "divination" -> Color.MAGENTA;
			case "enchantment" -> Color.PINK;
			case "evocation" -> Color.ORANGE;
			case "illusion" -> Color.CYAN;
			case "necromancy" -> Color.BLACK;
			default -> Color.GRAY;
		};
	}

	// Returns a bool that states if spell loading was successful or not
	private boolean loadSpells() {
		// If the spell map is already in memory dont load again
		if(spellMap != null) return true;

		Scanner spellScanner;
		try {
			File spellFile = new File("src/main/resources/spells.csv");
			spellScanner = new Scanner(spellFile);
		} catch (FileNotFoundException e) {
			logger.error("Unable to load spell list");
			return false;
		}

		// Get rid of the first line which just contains identifiers
		spellScanner.nextLine();
		spellMap = new HashMap<>();
		// Each line is a spell, so load in one spell at a time
		while(spellScanner.hasNextLine()){
			String line = spellScanner.nextLine();
			// Lines begin with the name of the spell
			String key = line.substring(0, line.indexOf(',')).toLowerCase();
			// (0: name, 1: source, 2: level, 3: school, 4: casting time, 5: range, 6: components, 7: duration, 8: description, 9: spell lists)
			String[] value = line.split(",");
			spellMap.put(key, value);
		}

		return true;
	}
}