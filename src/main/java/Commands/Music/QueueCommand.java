package Commands.Music;

import Core.Commands.ServerCommand;
import Core.Music.GuildMusicManager;
import Core.Music.MusicUtils;
import Core.Music.WylxPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.time.Duration;

public class QueueCommand extends ServerCommand {
    private static final int PAGE_COUNT = 10;

    QueueCommand() {
        super("queue", CommandPermission.EVERYONE);
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(event.getGuild().getIdLong());

        if (manager.isNotPlaying()) {
            event.getChannel().sendMessage("Wylx is not playing anything!").queue();
            return;
        }

        event.getChannel().sendMessage(getQueuePage(0, manager)).setActionRow(
                Button.secondary("queue:first", "\u23EA"),
                Button.secondary("queue:previous", "\u25C0"),
                Button.secondary("queue:next", "\u25B6"),
                Button.secondary("queue:last", "\u23E9")
        ).delay(Duration.ofSeconds(120)).queue(message -> {
            message.editMessageComponents().queue();
        });
    }

    public static String getQueuePage(int page, GuildMusicManager manager) {
        Object[] list = manager.getQueue();
        AudioTrack currentPlaying = manager.getCurrentTrack();
        StringBuilder builder = new StringBuilder();

        page = Math.min(page, list.length / PAGE_COUNT);
        long millis = MusicUtils.getTimeRemaining(list, currentPlaying);

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
                millis == -1 ? "Unknown" : MusicUtils.getPrettyDuration(Duration.ofMillis(millis)),
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
