package Commands.DND;

import Core.Events.ThreadedSilentEvent;
import Core.Wylx;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;
import java.util.regex.Pattern;

public class DiceRoll extends ThreadedSilentEvent {
	private final Random random = new Random();

	public DiceRoll(){
		super("Roll <x>d<y> dice, can separate arguments with spaces to roll multiple dice", 1000);
	}

	@Override
	public boolean check(MessageReceivedEvent event) {
		String msg = event.getMessage().getContentRaw();
		String prefix = Wylx.getInstance().getPrefixThanksJosh(event.getGuild().getIdLong());
		if(!msg.startsWith(prefix)) return false;
		return Pattern.matches("\\d*d\\d.*", msg.substring(prefix.length()));
	}

	@Override
	protected void runEventThread(MessageReceivedEvent event) {
		event.getMessage().getChannel().sendTyping().queue();
		String message = event.getMessage().getContentRaw().toLowerCase().replace(Wylx.getInstance().getPrefixThanksJosh(event.getGuild().getIdLong()), "");
		String[] elms = message.split(" ");
		int total = 0;
		int mult = 1;
		String outputString = "";

		for (String elm : elms) {
			if (elm.equals("-")) {
				mult = -1;
			} else if (elm.equals("+")) {
				mult = 1;
			} else if (elm.contains("d")) {
				outputString += (mult == -1 ? " - " : " + ");
				String[] diceParts = elm.split("d");
				if (diceParts.length > 2) {
					continue;
				}
				outputString += "[";
				int num;
				int sides;
				try {
					num = diceParts[0].equals("") ? 1 : Integer.parseInt(diceParts[0]);
					sides = Integer.parseInt(diceParts[1]);
				} catch (NumberFormatException e){
					continue;
				}
				for (int i = 0; i < num; i++) {
					int rand = random.nextInt(sides) + 1;
					outputString += rand;
					if (i != num - 1) outputString += ", ";
					total += mult * rand;
				}
				outputString += "]";
			} else {
				try {
					int change = Integer.parseInt(elm);
					total += mult * Integer.parseInt(elm);
					outputString += (mult == -1 ? " - " : " + ") + change;
				} catch (NumberFormatException e) {}
			}
		}
		event.getMessage().reply("```diff\n" +
				"- " + total + "\n" +
				outputString + "\n" +
				"```").queue();

	}
}
