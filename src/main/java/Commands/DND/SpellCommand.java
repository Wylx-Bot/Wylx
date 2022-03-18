package Commands.DND;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class SpellCommand extends ServerCommand {
	// Spells are stored at their name (lowercase)
	// Value is a string array (name, source, school, casting time, range, components, duration, description, spell lists)
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
		if(ctx.args().length == 1){
			spellList(ctx);
		} else {
			spellSearch(ctx);
		}
	}

	private void spellList(CommandContext ctx) {
		// Load spells, error on fail
		if(!loadSpells()) return;
		String message = "";
		for(String name : spellMap.keySet().stream().sorted().toList()){
			message += name + "\n";
			if(message.length() > 1900){
				ctx.event().getChannel().sendMessage(message).queue();
				message = "";
			}
		}
		ctx.event().getChannel().sendMessage(message).queue();
	}

	private void spellSearch(CommandContext ctx){
		// Load spells, error on fail
		if(!loadSpells()) return;
		String spellName = ctx.event().getMessage().getContentRaw().toLowerCase().substring(ctx.args()[0].length() + 1);
		ctx.event().getMessage().reply("search " + spellName).queue();
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
			System.out.println(line);
			// Lines begin with the name of the spell
			String key = line.substring(0, line.indexOf(',')).toLowerCase();
			// (name, source, school, casting time, range, components, duration, description, spell lists)
			String[] value = line.split(",");
			spellMap.put(key, value);
		}

		return true;
	}
}