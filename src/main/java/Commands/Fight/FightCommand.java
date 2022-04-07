package Commands.Fight;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ThreadedCommand;
import Core.Fight.FightUserManager;
import Core.Fight.FightUserStats;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FightCommand extends ThreadedCommand {

    private FightUserManager isFightingList = new FightUserManager();
    private double[] damageArray = { 25, 50, 75, 100, 125, 150, 200, 250 };

    private final Pattern mentionPattern = Message.MentionType.USER.getPattern();

    public FightCommand() {
        super("fight", CommandPermission.EVERYONE, "Fight another user");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        Message msg = ctx.event().getMessage();
        User player1 = msg.getAuthor(), player2;

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
        player2 = event.getJDA().retrieveUserById(userid).complete();
        if (player2 == null) {
            msg.reply("User does not exist").queue();
            return;
        }

        if (player2.isBot() || msg.getAuthor().isBot()) {
            // TODO: Fancy bot message
            msg.reply("One of the users is a bot!").queue();
            return;
        }

        if (player1.getIdLong() == player2.getIdLong()) {
            msg.reply("You can't fight yourself!").queue();
            return;
        }

        // Check if users are fighting
        if (isFightingList.userIsFighting(player1) ||
            isFightingList.userIsFighting(player2)) {
            msg.reply("One of the above users is currently fighting! Please wait").queue();
            return;
        }

        // TODO: Get stats

        fight (ctx, new FightUserStats(player1), new FightUserStats(player2));

        // TODO: Save stats
    }

    private void fight(CommandContext ctx, FightUserStats player1, FightUserStats player2) {
        MessageChannel channel = ctx.event().getChannel();
        while (player1.hp > 0 && player2.hp > 0) {
            break;
        }
    }
}
