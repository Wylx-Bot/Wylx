package Commands.ServerSettings;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.Permission;

public class EnabledModules extends ServerCommand {
    public EnabledModules() {
        super("enabledmodules", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "List and set enabled modules");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        DiscordServer db = ctx.db();
        String[] args = ctx.args();
        if (args.length != 2) return;


        ctx.event().getMessage().reply("Changing prefix to " + args[1]).queue();
        db.setSetting(ServerIdentifiers.Prefix, args[1]);
    }
}
