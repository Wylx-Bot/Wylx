package Commands.ServerSettings;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Database.DiscordServer;
import Database.ServerIdentifiers;
import net.dv8tion.jda.api.Permission;

import java.util.Random;

public class SetPrefix extends ServerCommand {
    public SetPrefix() {
        super("setpwefix", CommandPermission.DISCORD_PERM, Permission.ADMINISTRATOR,
                "set pwefix fow sewvew ღ(U꒳Uღ)", "setprefix");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        DiscordServer db = ctx.db();
        String[] pawsable = new String[]{"uwu", "owo", "UwU", "OwO", "rawr", "RAWR", "XD", ":3", ""};
        Random random = new Random();
        String pick = pawsable[random.nextInt(pawsable.length)];
        ctx.event().getMessage().reply("Uwu cawn't change thawt...\n But i cawn *wink*\n Changing pwefix tuwu" + pick).queue();
        db.setSetting(ServerIdentifiers.Prefix, pick);
    }
}
