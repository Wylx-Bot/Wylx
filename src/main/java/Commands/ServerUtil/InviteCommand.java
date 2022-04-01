package Commands.ServerUtil;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InviteCommand extends ServerCommand {
    private final static String URL = "https://discord.com/api/oauth2/authorize?client_id=933557997328793691&permissions=277293853696&scope=bot";

    public InviteCommand() {
        super("inwite", CommandPermission.EVERYONE, "Gewt a wink tuwu add the bot tuwu youw sewvew ( ˊ.ᴗˋ )... Yaaa more frewnds", "invite");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        event.getChannel().sendMessage("Cwick hewe tuwu invite me tuwu youw sewvew *blushes* UWU: " + URL).queue();
    }
}
