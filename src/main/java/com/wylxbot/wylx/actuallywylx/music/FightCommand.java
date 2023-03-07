package com.wylxbot.wylx.actuallywylx.music;

import com.wylxbot.wylx.Commands.Fight.Util.*;
import com.wylxbot.wylx.actuallywylx.WylxCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class FightCommand extends WylxCommand {
    private final Random random = new Random();
    private static final FightUserManager userManager = FightUserManager.getInstance();
    private final String noExpStr = "\nThere is no EXP to be found here";

    private void fight(SlashCommandInteraction event, String header, FightUserStats player1, FightUserStats player2) {
        // Reset the HP of our contestants
        player1.resetHP();
        player2.resetHP();
        String msg = header;

        FightUserStats attacker, defender;

        // Use difference in speed when checking which player should go first
        double speedDiff = player1.getStatMultiplier(FightStatTypes.SPEED) - player2.getStatMultiplier(FightStatTypes.SPEED);
        boolean player1Turn = random.nextDouble() < (speedDiff + 0.5);

        do {
            attacker = player1Turn ? player1 : player2;
            defender = player1Turn ? player2 : player1;

            int damage = (int) (getRandomDamage() * attacker.getStatMultiplier(FightStatTypes.DAMAGE));
            defender.hp -= Math.min(damage, defender.hp);

            String attackMessage = FightMessages.attackMessages[random.nextInt(FightMessages.attackMessages.length)];

            if (defender.hp <= 0) {
                // TODO: Finisher move
            }

            attackMessage = replaceStringWithUsers(attackMessage, attacker, defender);
            msg += String.format("%s! [-%d] [%d hp left]\n", attackMessage, damage, defender.hp);
            event.getHook().editOriginal(msg).complete();

            player1Turn = !player1Turn;

            try {
                Thread.sleep(1200);
            } catch (Exception e) {
                // NOOP
            }
        } while (player1.hp > 0 && player2.hp > 0);

        int exp = (int) (random.nextDouble() * 10) + 15;
        int attackerExp = (int) (exp * 2 * attacker.getStatMultiplier(FightStatTypes.EXP));
        int defenderExp = (int) (exp / 3 * defender.getStatMultiplier(FightStatTypes.EXP));

       String result = String.format("%s won and gained %d EXP!\n%s lost but gained %d EXP.\n\n",
                attacker.user.getEffectiveName(), attackerExp,
                defender.user.getEffectiveName(), defenderExp);

        if (attacker.addExp(attackerExp)) {
            result += String.format("%s leveled up! New level %d\n",
                    attacker.user.getEffectiveName(), attacker.getLvl());
        }

        if (defender.addExp(defenderExp)) {
            result += String.format("%s leveled up! New level %d",
                    defender.user.getEffectiveName(), defender.getLvl());
        }

        msg += "\n" + result;

        event.getHook().editOriginal(msg).queue();

        // Fight is done, save and let other fights occur
        player1.save();
        player2.save();
        userManager.setUserFightStatus(player1.user, UserFightStatus.NONE);
        userManager.setUserFightStatus(player2.user, UserFightStatus.NONE);

        try {
            Thread.sleep(30_000);
        } catch (InterruptedException e) {
        }

        event.getHook().editOriginal(header + result).queue();

    }

    private double getRandomDamage() {
        double randomPercent = random.nextDouble();
        // (1.4x - 0.49) ^ 3 + 0.25
        //\left(9.38x-3.28\right)^{3}+60
        return Math.pow(9.38 * randomPercent - 3.28, 3) + 60;
    }

    private String replaceStringWithUsers(String msg, FightUserStats attacker, FightUserStats defender) {
        msg = msg.replace("{p1}", String.format("**%s**", attacker.user.getEffectiveName()));
        msg = msg.replace("{p2}", String.format("**%s**", defender.user.getEffectiveName()));
        msg = msg.replace("{r}", Integer.toString(random.nextInt(8) + 2));
        return msg;
    }

    @Override
    public SlashCommandData getSlashCommand() {
        return Commands.slash("fight", "Fight another user")
                .addOption(OptionType.USER, "user", "User to fight", true);
    }

    @Override
    public void doStuff(SlashCommandInteraction interaction) {
        Member player1 = interaction.getMember();
        Member player2 = interaction.getOption("user", OptionMapping::getAsMember);

        UserFightStatus player1Status = userManager.getUserStatus(player1);
        UserFightStatus player2Status = userManager.getUserStatus(player2);

        // Check if users are fighting
        if (player1Status == UserFightStatus.FIGHTING ||
            player2Status == UserFightStatus.FIGHTING) {
            interaction.reply("One of the above users is currently fighting! Please wait").queue();
            return;
        }

        // Users cannot be changing SP when they go to fight
        if (player1Status == UserFightStatus.SKILLPOINTS ||
            player2Status == UserFightStatus.SKILLPOINTS) {
            interaction.reply("Please finish spending skill points before fighting").queue();
            return;
        }

        // Prevent the user from fighting multiple times
        userManager.setUserFightStatus(player1, UserFightStatus.FIGHTING);
        userManager.setUserFightStatus(player2, UserFightStatus.FIGHTING);

        FightUserStats player1Stats = FightUserStats.getUserStats(player1);
        FightUserStats player2Stats = FightUserStats.getUserStats(player2);

        String headerStr = String.format("__**%s** [lvl: %d] vs **%s** [lvl: %d]__\n",
                player1.getEffectiveName(), player1Stats.getLvl(),
                player2.getEffectiveName(), player2Stats.getLvl());

        interaction.reply(headerStr).complete();

        if (player2.getUser().isBot() || player1.getUser().isBot()) {
            // Bot should be user 1, as they do the attack
            FightUserStats bot = player1.getUser().isBot() ? player1Stats : player2Stats;
            FightUserStats user = player1.getUser().isBot() ? player2Stats : player1Stats;

            // Get attack msg
            int idx = random.nextInt(FightMessages.botMessages.length);
            String botMsg = FightMessages.botMessages[idx];
            botMsg = replaceStringWithUsers(botMsg, bot, user);

            headerStr += "\n" + botMsg + noExpStr;
            interaction.getHook().editOriginal(headerStr).queue();
            userManager.setUserFightStatus(player1, UserFightStatus.NONE);
            userManager.setUserFightStatus(player2, UserFightStatus.NONE);
            return;
        }

        if (player1.getIdLong() == player2.getIdLong()) {
            // Get attack msg
            int idx = random.nextInt(FightMessages.duplicateFighterMessages.length);
            String dupMsg = FightMessages.duplicateFighterMessages[idx];
            dupMsg = replaceStringWithUsers(dupMsg, player1Stats, player2Stats);

            headerStr += dupMsg + noExpStr;
            interaction.getHook().editOriginal(headerStr).queue();
            userManager.setUserFightStatus(player1, UserFightStatus.NONE);
            userManager.setUserFightStatus(player2, UserFightStatus.NONE);
            return;
        }

        // Do fight loop and decide victor
        fight (interaction, headerStr, player1Stats, player2Stats);
    }
}
