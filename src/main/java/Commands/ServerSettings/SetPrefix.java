package Commands.ServerSettings;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.Permission;

public class SetPrefix extends ServerCommand {
    public SetPrefix() {
        super("setprefix", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "set prefix for server");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        String[] args = ctx.args();
        if (args.length != 2) return;
        ctx.event().getMessage().reply("Changing prefix to " + args[1]).queue();
        ctx.db().setSetting(ctx.guildID(), ServerIdentifiers.Prefix, args[1]);
    }
}
