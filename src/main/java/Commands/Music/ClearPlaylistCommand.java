package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;

public class ClearPlaylistCommand extends ServerCommand {
    ClearPlaylistCommand() {
        // TODO: Change perm
        super("clearplaylist",
                CommandPermission.EVERYONE,
                "Cweaw pwaywist and stop cuwwent twack",
                "cp", "stop");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        ctx.musicManager().clearQueue();
    }
}
