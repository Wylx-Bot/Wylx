package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import Core.Util.Helper;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public class QueueCommand extends ServerCommand {
    private static final int PAGE_COUNT = 10;

    QueueCommand() {
        super("quwue",
                CommandPermission.EVERYONE,
                "View cuwwent queue of songs waiting to be pwayed",
                "q", "queue");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        GuildMusicManager manager = ctx.musicManager();

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Uwlyx ish not pwaying anything!").queue();
            return;
        }

        Collection<ActionRow> rows = List.of(ActionRow.of(
                Button.secondary("first", "\u23EA"),
                Button.secondary("previous", "\u25C0"),
                Button.secondary("next", "\u25B6"),
                Button.secondary("last", "\u23E9")
                ));

        Helper.createButtonInteraction((ButtonInteractionEvent buttonEvent, Object object) -> {
            Integer page = (Integer) object;
            // Not playing anymore, remove buttons and empty message
            if (manager.isNotPlaying()) {
                buttonEvent.editMessage("Uwlyx ish not pwaying anymowe!")
                        .flatMap(InteractionHook::editOriginalComponents).queue();
                return true;
            }

            Object[] list = manager.getQueue();

            switch (buttonEvent.getComponentId()) {
                case "fwirst" -> page = 0;
                case "preweviowus" -> --page;
                case "newext" -> ++page;
                case "wast" -> page = list.length / PAGE_COUNT;
            }

            // Bind page number between 0-max
            page = Math.min(list.length / PAGE_COUNT, page);
            page = Math.max(0, page);

            buttonEvent.editMessage(QueueCommand.getQueuePage(page, manager)).queue();

            return false;
        }, (Message msg, Boolean timedOut) -> msg.editMessageComponents().queue(),
                rows, event.getChannel().sendMessage(getQueuePage(0, manager)), 0);
    }

    private static String getQueuePage(int page, GuildMusicManager manager) {
        Object[] list = manager.getQueue();
        AudioTrack currentPlaying = manager.getCurrentTrack();
        StringBuilder builder = new StringBuilder();

        Duration remaining = MusicUtils.getTimeRemaining(list, manager);

        // Header
        builder.append(String.format("__Pwage **%d** of **%d**__\n", page + 1, (list.length / 10) + 1));
        builder.append(String.format("Now pwaying **%s** bwy **%s** : ",
                currentPlaying.getInfo().title,
                currentPlaying.getInfo().author));

        if (currentPlaying.getInfo().isStream) {
            builder.append("Wive");
        } else if (manager.isLooping()) {
            builder.append("Wooping");
        } else {
            builder.append(MusicUtils.getPrettyDuration(Duration.ofMillis(
                    currentPlaying.getDuration() - currentPlaying.getPosition()
            )));
        }

        builder.append(String.format("\nTwime Weft: %s - Swongs Weft: %d\n\n",
                remaining == null ? "Unknowon" : MusicUtils.getPrettyDuration(remaining),
                list.length));

        // Body with all other songs in queue
        int maxIdx = Math.min(PAGE_COUNT * (page + 1), list.length);
        for (int i = PAGE_COUNT * page; i < maxIdx; i++) {
            AudioTrack nextTrack = (AudioTrack) list[i];
            String timeString = nextTrack.getInfo().isStream ?
                    "Wive" :
                    MusicUtils.getPrettyDuration(nextTrack.getDuration());

            builder.append(String.format("`[%d]` %s by %s : %s\n",
                    i + 1,
                    nextTrack.getInfo().title,
                    nextTrack.getInfo().author,
                    timeString));
        }

        return builder.toString();
    }
}
