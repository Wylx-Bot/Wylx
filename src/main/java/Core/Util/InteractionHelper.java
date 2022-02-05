package Core.Util;

import Core.Wylx;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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

public class InteractionHelper {

    public static void sendTemporaryMessage(MessageAction msg, Duration timeout) {
        sendTemporaryMessage(null, null, msg, timeout);
    }

    public static void sendTemporaryMessage(Consumer<Message> sendSuccess,
                                            Consumer<Void> endSuccess,
                                            MessageAction msg,
                                            Duration timeout) {
        msg.queue(message -> {
            if (sendSuccess != null) sendSuccess.accept(message);
            message.delete().queueAfter(timeout.toSeconds(), TimeUnit.SECONDS, endSuccess,
                    new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
        });
    }

    public static void createButtonInteraction(BiFunction<ButtonInteractionEvent, Object, Boolean> interactionRunnable,
                                               BiConsumer<Message, Object> interactionEndRunnable,
                                               Collection<ActionRow> actionRows,
                                               MessageAction toSend,
                                               Object ctx) {

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
                    interactionEndRunnable.accept(msg, ctx);
                }
            }
        };

        jda.addEventListener(adapter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                interactionEndRunnable.accept(msg, ctx);
                timer.cancel();
            }
        }, 1000 * 120);

    }
}
