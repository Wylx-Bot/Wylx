package com.wylxbot.wylx.Core.Util;

import com.wylxbot.wylx.Wylx;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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
            ErrorHandler errorHandler = new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE);
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

    private final static List<String> numbToEmoji = Arrays.asList("U+30U+fe0fU+20e3", "U+31U+fe0fU+20e3", "U+32U+fe0fU+20e3", "U+33U+fe0fU+20e3", "U+34U+fe0fU+20e3",
            "U+35U+fe0fU+20e3", "U+36U+fe0fU+20e3", "U+37U+fe0fU+20e3", "U+38U+fe0fU+20e3", "U+39U+fe0fU+20e3");

    /**
     * Adds emoji to the supplied message corresponding to the provided consumers, on the user adding a reaction to one
     * of the emoji the corresponding consumer is called and given the event, the listener then removes itself
     * The listener also times out after 5 minutes, if the user does not respond in this time their response will not
     * be processed
     * @param msg The message that reactions will be added to / listened to
     * @param user The user allowed to interact with the message, null for everyone
     * @param consumers List of consumers, length must be in range [1, 9]
     */
    public static void chooseFromListWithReactions(Message msg, User user, Consumer<MessageReactionAddEvent> ... consumers){
        if(consumers.length > 9 || consumers.length < 1) throw new IllegalArgumentException("Consumer count must be between 1 and 9");

        Wylx.getInstance().getJDA().addEventListener(new ReactionListenerContainer(msg, user, consumers));
    }

    private static class ReactionListenerContainer extends ListenerAdapter{
        private final Consumer<MessageReactionAddEvent>[] consumerList;
        private final Message msg;
        private final User user;

        private final Timer timer = new Timer();
        private final static long FIVE_MINUTES_IN_MS = 300000;

        private ReactionListenerContainer(Message msg, User user, Consumer<MessageReactionAddEvent>[] consumerList){
            this.consumerList = consumerList;
            this.msg = msg;
            this.user = user;

            for(int i = 0; i < consumerList.length; i++){
                msg.addReaction(numbToEmoji.get(i+1)).queue();
            }
            msg.addReaction(X).queue();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    quit();
                }
            }, FIVE_MINUTES_IN_MS);
        }

        @Override
        public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
            if(!event.getMessageId().equals(msg.getId()) ||
                    (user != null && !event.getUserId().equals(user.getId()))) return;

            // Quit emote
            if(event.getReactionEmote().getAsCodepoints().equals(X)){
                quit();
                return;
            }

            // If not valid emoji return
            if(!numbToEmoji.contains(event.getReactionEmote().getAsCodepoints())) return;

            consumerList[Character.getNumericValue(event.getReactionEmote().getEmoji().charAt(0)) - 1].accept(event);
            quit();
        }

        private void quit(){
            timer.cancel();
            msg.delete().queue();
            msg.getJDA().removeEventListener(this);
        }
    }
}
