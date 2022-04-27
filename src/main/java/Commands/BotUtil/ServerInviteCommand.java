package Commands.BotUtil;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;

public class ServerInviteCommand extends ServerCommand {
    private final static String INVITE_LINK = "https://discord.gg/8cNgukpdgR";

    public ServerInviteCommand() {
        super("serverinvite", CommandPermission.EVERYONE, "Provides an invite link to the server Wylx gets developed on, report bugs and help us come up with new features!");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        ctx.event().getChannel().sendMessage(INVITE_LINK).queue();
    }
}
