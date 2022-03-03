package Core.Util;

import Core.Wylx;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Helper {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int ONE_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

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

    /**
     * Create a self-destructing message which deletes itself after timeout
     * @param msg Message to send
     * @param timeout Time until message is deleted
     */
    public static void selfDestructingMsg(MessageAction msg, Duration timeout) {
        msg.queue(message -> {
            var errorHandler = new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE);
            message.delete().queueAfter(timeout.toSeconds(), TimeUnit.SECONDS, null, errorHandler);
        });
    }

    public static void validateButtons(String description, MessageReceivedEvent event, Runnable runnable) {
        long messageId = event.getMessageIdLong();
        List<ActionRow> rows = List.of(ActionRow.of(
                Button.success(CHECK, "Continue"),
                Button.danger(X, "Cancel")
        ));

        createButtonInteraction((ButtonInteractionEvent buttonEvent, Object ctx) -> {
            if (buttonEvent.getUser().getIdLong() == event.getAuthor().getIdLong()) {
                if (buttonEvent.getComponentId().equals(CHECK)) {
                    event.getChannel().deleteMessageById(messageId).queue();
                    runnable.run();
                } else {
                    buttonEvent.editMessage("Cancelled")
                            .flatMap(InteractionHook::editOriginalComponents)
                            .queue();
                }
                return true;
            }
            return false;
        }, (Message sentMessage, Boolean timedOut) -> {
            if (timedOut) {
                sentMessage.editMessage("Timed Out")
                        .flatMap(Message::editMessageComponents).queue();
            }
        }, rows, event.getChannel().sendMessage(description), null);
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
        JDA jda = Wylx.getInstance().getJDA();

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
