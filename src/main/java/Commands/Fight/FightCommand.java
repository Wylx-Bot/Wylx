package Commands.Fight;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ThreadedCommand;
import Core.Fight.FightUserManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FightCommand extends ThreadedCommand {

    private FightUserManager isFightingList = new FightUserManager();

    private final Pattern mentionPattern = Message.MentionType.USER.getPattern();

    public FightCommand() {
        super("fight", CommandPermission.EVERYONE, "Fight another user");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        Message msg = ctx.event().getMessage();

        if (msg.mentionsEveryone()) {
            msg.reply("How are you supposed to fight everyone here? That seems *really* difficult...").queue();
            return;
        }

        // Check for mentioned users
        Matcher matcher = mentionPattern.matcher(ctx.parsedMsg());
        if (!matcher.find()) {
            msg.reply("Please mention a user").queue();
            return;
        }

        // ID is in group 1
        String userid = matcher.group(1);
        User userObj = event.getJDA().getUserById(userid);
        if (userObj == null) {
            msg.reply("User does not exist").queue();
            return;
        }

        // Check if user and mentioned users are not bots

        // Check if users are fighting

        // Check is fighting

        //
    }
}
