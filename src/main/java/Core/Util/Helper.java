package Core.Util;

import Core.Wylx;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Helper {
	private static final int MILLIS_PER_SECOND = 1000;
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int TWO_MINUTES = MILLIS_PER_SECOND * SECONDS_PER_MINUTE * 2;

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

	public static void sendTemporaryMessage(MessageAction msg, Duration timeout) {
		msg.queue(message -> message.delete().queueAfter(timeout.toSeconds(), TimeUnit.SECONDS, null,
				new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)));
	}

	public static void createButtonInteraction(BiFunction<ButtonInteractionEvent, Object, Boolean> interactionRunnable,
											   BiConsumer<Message, Boolean> interactionEndRunnable,
											   Collection<ActionRow> actionRows,
											   MessageAction toSend,
											   Object ctx) {

		Timer timer = new Timer();
		Message msg = toSend.setActionRows(actionRows).complete();
		JDA jda = Wylx.getInstance().getJDA();

		ListenerAdapter adapter = new ListenerAdapter() {
			@Override
			public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
				// Not the message we sent
				if (event.getMessage().getIdLong() != msg.getIdLong()) {
					return;
				}

				if (interactionRunnable.apply(event, ctx)) {
					timer.cancel();
					jda.removeEventListener(this);
					interactionEndRunnable.accept(msg, false);
				}
			}
		};

		jda.addEventListener(adapter);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				jda.removeEventListener(adapter);
				interactionEndRunnable.accept(msg, true);
				timer.cancel();
			}
		}, TWO_MINUTES);
	}
}
