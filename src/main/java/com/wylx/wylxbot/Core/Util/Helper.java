package com.wylx.wylxbot.Core.Util;

import com.wylx.wylxbot.Core.Wylx;
import java.time.Duration;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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

/**
 * Helper functions for getting user input after a command is run.
 */
public class Helper {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int ONE_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

    // Unicode for :x: and :check:
    public static final String CHECK = "U+2705";
    public static final String X = "U+274c";

    /**
     * Create a confirmation message which users can respond to by selecting :check: or :x:.
     * runnable only runs when :Check: is selected.
     *
     * @param description Description to put in confirmation message
     * @param event Original event that caused the command that called this
     * @param runnable Runnable that is run when :check: is selected.
     */
    public static void validate(String description, MessageReceivedEvent event, Runnable runnable) {
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
        private final long messageId;
        private final long userId;

        private ValidateContainer(Runnable runnable, long messageId, long userId) {
            this.runnable = runnable;
            this.messageId = messageId;
            this.userId = userId;
            Wylx.getInstance().getJda().addEventListener(this);
        }

        @Override
        public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
            if (event.getMessageIdLong() == messageId && event.getUserIdLong() == userId) {
                if (event.getReactionEmote().getAsCodepoints().equals(CHECK)) {
                    event.getChannel().deleteMessageById(messageId).queue();
                    runnable.run();
                    Wylx.getInstance().getJda().removeEventListener(this);
                } else if (event.getReactionEmote().getAsCodepoints().equals(X)) {
                    event.getChannel().sendMessage("Cancelled").queue();
                    Wylx.getInstance().getJda().removeEventListener(this);
                }
            }
        }
    }

    /**
     * Create a self-destructing message which deletes itself after timeout.
     *
     * @param msg Message to send
     * @param timeout Time until message is deleted
     */
    public static void selfDestructingMsg(MessageAction msg, Duration timeout) {
        msg.queue(message -> {
            var errorHandler = new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE);
            message.delete().queueAfter(timeout.toSeconds(), TimeUnit.SECONDS, null, errorHandler);
        });
    }

    /**
     * Helper function to create button interactions on a message.
     * The buttons remain until either interactionRunnable returns true, or a timeout occurs after 2 minutes.
     *
     * @param interactionRunnable (ButtonInteractionEvent, Object == ctx) -> Boolean
     *                            Ran anytime a button is pressed on the method
     * @param interactionEndRunnable (Message, Boolean == timed out) -> Void
     *                               Ran once when buttons are removed.
     * @param actionRows Action Rows which contain buttons
     * @param toSend Message to be sent with buttons
     * @param ctx Context to be passed into interactionRunnable.
     *            Useful for keeping state between calls of interactionRunnable
     */
    public static void createButtonInteraction(BiFunction<ButtonInteractionEvent, Object, Boolean> interactionRunnable,
                                               BiConsumer<Message, Boolean> interactionEndRunnable,
                                               Collection<ActionRow> actionRows,
                                               MessageAction toSend,
                                               Object ctx) {

        Timer timer = new Timer();
        Message msg = toSend.setActionRows(actionRows).complete();
        JDA jda = Wylx.getInstance().getJda();

        ListenerAdapter adapter = new ListenerAdapter() {
            @Override
            public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
                // Not the message we sent
                if (event.getMessage().getIdLong() != msg.getIdLong()) {
                    return;
                }

                if (interactionRunnable.apply(event, ctx)) {
                    jda.removeEventListener(this);
                    timer.cancel();
                    interactionEndRunnable.accept(msg, false);
                }
            }
        };

        jda.addEventListener(adapter);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                jda.removeEventListener(adapter);
                timer.cancel();
                interactionEndRunnable.accept(msg, true);
            }
        }, ONE_HOUR);
    }
}
