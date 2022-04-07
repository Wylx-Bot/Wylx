package Commands.Fight;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ThreadedCommand;
import Core.Fight.FightMessages;
import Core.Fight.FightUserManager;
import Core.Fight.FightUserStats;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FightCommand extends ThreadedCommand {

    private final Random random = new Random();
    private FightUserManager isFightingList = new FightUserManager();
    private int[] damageArray = { 25, 30, 40, 50, 60, 75, 80, 90, 99, 100, 125, 150, 175, 200, 250 };

    private final Pattern mentionPattern = Message.MentionType.USER.getPattern();

    public FightCommand() {
        super("fight", CommandPermission.EVERYONE, "Fight another user");
    }

    @Override
    protected void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        Message msg = ctx.event().getMessage();
        Member player1 = msg.getMember(), player2;

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
        player2 = event.getGuild().retrieveMemberById(userid).complete();
        if (player2 == null) {
            msg.reply("User does not exist").queue();
            return;
        }

        // Check if users are fighting
        if (isFightingList.userIsFighting(player1) ||
            isFightingList.userIsFighting(player2)) {
            msg.reply("One of the above users is currently fighting! Please wait").queue();
            return;
        }

        // TODO: Get stats
        FightUserStats player1Stats = new FightUserStats(player1);
        FightUserStats player2Stats = new FightUserStats(player2);

        String headerStr = String.format("__**%s** [lvl: %d] vs **%s**__ [lvl: %d]",
                player1.getEffectiveName(), player1Stats.getLvl(),
                player2.getEffectiveName(), player2Stats.getLvl());

        msg.getChannel().sendMessage(headerStr).complete();


        if (player2.getUser().isBot() || msg.getAuthor().isBot()) {
            // TODO: Fancy bot message
            msg.reply("One of the users is a bot!").queue();

            return;
        }

//        if (player1.getIdLong() == player2.getIdLong()) {
//            msg.reply("You can't fight yourself!").queue();
//            return;
//        }


        fight (ctx, player1Stats, player2Stats);

        // TODO: Save stats
    }

    private void fight(CommandContext ctx, FightUserStats player1, FightUserStats player2) {
        boolean player1Turn = random.nextBoolean();
        ArrayList<Message> messages = new ArrayList<>();
        MessageChannel channel = ctx.event().getChannel();
        FightUserStats attacker = null;
        FightUserStats defender = null;

        while (player1.hp > 0 && player2.hp > 0) {
            attacker = player1Turn ? player1 : player2;
            defender = player1Turn ? player2 : player1;

            int damage = damageArray[random.nextInt(damageArray.length)];
            defender.hp -= Math.min(damage, defender.hp);

            String attackMessage = FightMessages.attackMessages[random.nextInt(FightMessages.attackMessages.length)];

            if (defender.hp <= 0) {
                // TODO: Finisher move
            }

            attackMessage = attackMessage.replace("{p1}", String.format("**%s**", attacker.user.getEffectiveName()));
            attackMessage = attackMessage.replace("{p2}", String.format("**%s**", defender.user.getEffectiveName()));
            attackMessage = attackMessage.replace("{r}", Integer.toString(random.nextInt(2,10)));

            String msgStr = String.format("%s! [-%d] [%d hp left]", attackMessage, damage, defender.hp);
            Message msgObj = channel.sendMessage(msgStr).complete();
            messages.add(msgObj);

            try {
                Thread.sleep(1200);
            } catch (Exception e) {
                // NOOP
            }
        }

        int exp = (int) (random.nextDouble() * 10) + 15;
        String fightEnd = String.format("%s won and gained %d EXP!\n%s lost but gained %d EXP.\n\n",
                attacker.user.getEffectiveName(), exp * 2,
                defender.user.getEffectiveName(), exp / 3);

        if (attacker.addExp(exp * 2)) {
            fightEnd += String.format("%s leveled up! New level %d\n",
                    attacker.user.getEffectiveName(), attacker.getLvl());
        }

        if (defender.addExp(exp / 3)) {
            fightEnd += String.format("%s leveled up! New level %d",
                    defender.user.getEffectiveName(), attacker.getLvl());
        }

        channel.sendMessage(fightEnd).complete();
        channel.purgeMessages(messages);
    }
}
