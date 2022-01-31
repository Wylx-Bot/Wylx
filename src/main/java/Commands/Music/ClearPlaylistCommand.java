package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Music.WylxPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearPlaylistCommand extends ServerCommand {
    ClearPlaylistCommand() {
        // TODO: Change perm
        super("clearplaylist",
                CommandPermission.EVERYONE,
                "Clear playlist and stop current track",
                "cp");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, CommandContext ctx) {
        var manager = WylxPlayerManager.getInstance().getGuildManager(ctx.guildID());
        manager.clearQueue();
    }
}
