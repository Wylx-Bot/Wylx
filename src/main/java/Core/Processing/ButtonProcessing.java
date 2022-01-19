package Core.Processing;

import Commands.Music.QueueCommand;
import Core.Music.GuildMusicManager;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ButtonProcessing extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (!event.isFromGuild()) return;

        long guildID = Objects.requireNonNull(event.getGuild()).getIdLong();
        String id = event.getComponentId();
        String[] args = id.split(":");

        var message = event.getMessage();

        if ("queue".equals(args[0])) {
            GuildMusicManager manager = WylxPlayerManager.getInstance().getGuildManager(guildID);

            // Not playing anymore, no queue
            if (manager.isNotPlaying()) {
                event.editMessage("Wylx is not playing anymore!")
                        .flatMap(InteractionHook::editOriginalComponents).queue();
                return;
            }

            // Extract current page from queue message
            String msgStr = message.getContentStripped();
            int page = 0;
            for (int i = 5; i < msgStr.length(); i++) {
                if (Character.isDigit(msgStr.charAt(i))) continue;
                page = Integer.parseInt(msgStr.substring(5, i)) - 1;
                break;
            }

            switch (args[1]) {
                case "first" -> page = 0;
                case "previous" -> --page;
                case "next" -> ++page;
                case "last" -> page = Integer.MAX_VALUE;
            }

            event.editMessage(QueueCommand.getQueuePage(page, manager)).queue();
        }
    }
}
