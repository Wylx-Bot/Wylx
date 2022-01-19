package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class QueueCommand extends ServerCommand {
    private static final int PAGE_COUNT = 10;

    QueueCommand() {
        super("queue", CommandPermission.EVERYONE);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        GuildMusicManager manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing anything!").queue();
            return;
        }

        AtomicLong msgId = new AtomicLong();

        ListenerAdapter adapter = new ListenerAdapter() {
            int page = 0;

            @Override
            public void onButtonClick(@NotNull ButtonClickEvent event) {
                // Not the message we sent
                if (event.getMessage().getIdLong() != msgId.longValue()) {
                    return;
                }

                // Not playing anymore, remove buttons and empty message
                if (manager.isNotPlaying()) {
                    event.editMessage("Wylx is not playing anymore!")
                            .flatMap(InteractionHook::editOriginalComponents).queue();
                    return;
                }

                switch (event.getComponentId()) {
                    case "first" -> page = 0;
                    case "previous" -> --page;
                    case "next" -> ++page;
                    case "last" -> page = Integer.MAX_VALUE;
                }

                event.editMessage(QueueCommand.getQueuePage(page, manager)).queue();
            }
        };

        event.getChannel().sendMessage(getQueuePage(0, manager)).setActionRow(
                Button.secondary("first", "\u23EA"),
                Button.secondary("previous", "\u25C0"),
                Button.secondary("next", "\u25B6"),
                Button.secondary("last", "\u23E9")
        ).queue(message -> {
            message.editMessageComponents().queueAfter(30, TimeUnit.SECONDS, msg -> {
                event.getJDA().removeEventListener(adapter);
            });
            msgId.set(message.getIdLong());
        });

        event.getJDA().addEventListener(adapter);
    }

    public static String getQueuePage(int page, GuildMusicManager manager) {
        Object[] list = manager.getQueue();
        AudioTrack currentPlaying = manager.getCurrentTrack();
        StringBuilder builder = new StringBuilder();

        page = Math.min(page, list.length / PAGE_COUNT);
        Duration remaining = MusicUtils.getTimeRemaining(list, currentPlaying);

        builder.append(String.format("__Page **%d** of **%d**__\n", page + 1, (list.length / 10) + 1));
        builder.append(String.format("Now playing **%s** by **%s** : ",
                currentPlaying.getInfo().title,
                currentPlaying.getInfo().author));

        if (currentPlaying.getInfo().isStream) {
            builder.append("Live");
        } else {
            builder.append(MusicUtils.getPrettyDuration(Duration.ofMillis(
                    currentPlaying.getDuration() - currentPlaying.getPosition()
            )));
        }

        builder.append(String.format("\nTime Left: %s - Songs Left: %d\n\n",
                remaining == null ? "Unknown" : MusicUtils.getPrettyDuration(remaining),
                list.length));

        int maxIdx = Math.min(PAGE_COUNT * (page + 1), list.length);
        for (int i = PAGE_COUNT * page; i < maxIdx; i++) {
            AudioTrack nextTrack = (AudioTrack) list[i];
            String timeString = nextTrack.getInfo().isStream ?
                    "Live" :
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
