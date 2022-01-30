package Core.Util;

import Core.Wylx;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Helper {

	// Validates the users choice, if they select :check: it runs the runnable, if they select :x: do nothing
	public static final String CHECK = "U+2705";
	public static final String X = "U+274c";
	public static void validate(String description, MessageReceivedEvent event, Runnable runnable){
		event.getChannel().deleteMessageById(event.getMessageId()).queue();
		event.getChannel().sendMessage(description).queue(message -> {
			// Check emoji
			message.addReaction(CHECK).queue();
			// X emoji
			message.addReaction(X).queue();
			new ValidateContainer(runnable, message.getIdLong(), event.getAuthor().getIdLong());
		});
	}

	private static class ValidateContainer extends ListenerAdapter {
		private final Runnable runnable;
		private final long messageID;
		private final long userID;

		private ValidateContainer(Runnable runnable, long messageID, long userID){
			this.runnable = runnable;
			this.messageID = messageID;
			this.userID = userID;
			Wylx.getInstance().getJDA().addEventListener(this);
		}

		@Override
		public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
			if(event.getMessageIdLong() == messageID && event.getUserIdLong() == userID){
				if(event.getReactionEmote().getAsCodepoints().equals(CHECK)){
					event.getChannel().deleteMessageById(messageID).queue();
					runnable.run();
					Wylx.getInstance().getJDA().removeEventListener(this);
				} else if(event.getReactionEmote().getAsCodepoints().equals(X)){
					event.getChannel().sendMessage("Cancelled").queue();
					Wylx.getInstance().getJDA().removeEventListener(this);
				}
			}
		}
	}
}
