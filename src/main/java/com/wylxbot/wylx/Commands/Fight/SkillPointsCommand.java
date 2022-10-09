package Commands.Fight;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Fight.FightStatTypes;
import Core.Fight.FightUserStats;
import Core.Fight.FightUtil;
import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordUser;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.Collection;
import java.util.List;

public class SkillPointsCommand extends ServerCommand {

    public SkillPointsCommand() {
        super("skillpoints", CommandPermission.EVERYONE, "Provides the ping from the bot to discord",
                "sp", "skill");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        DatabaseManager db = Wylx.getInstance().getDb();
        Member user = ctx.event().getMember();
        assert user != null;

        String menuId = String.format("skill:%s:menu", ctx.event().getMessage().getId());
        SelectMenu menu = SelectMenu.create(menuId)
                .setPlaceholder("Please select a skill")
                .addOption("Attack", "atk")
                .addOption("Defense", "def")
                .addOption("EXP Multiplier", "exp")
                .addOption("Speed", "spd")
                .build();

        Collection<ActionRow> rows = List.of(ActionRow.of(
                    menu
                ), ActionRow.of(
                    Button.success("add", "+1"),
                    Button.danger("rem", "-1")
                )
        );

        DiscordUser dbUser = db.getUser(user.getId());
        FightUserStats stats = dbUser.getSetting(UserIdentifiers.FightStats);
        EmbedBuilder embed = new EmbedBuilder();

        int exp = FightUtil.calcEXPForLevel(stats.getLvl());
        int pointsToSpend = stats.getLvl() - stats.getUsedPoints();

        embed.setAuthor(user.getEffectiveName() + "'s stats in " + ctx.event().getGuild().getName(),
                null, user.getAvatarUrl());
        embed.setDescription("Level: " + stats.getLvl() + "\nEXP: (" + stats.getExp() + " / " + exp + ")");
        embed.addField("Stat Levels", String.format("HP: %d\nSpeed: %d\nDamage: %d\nEXP Multiplier: %d\n",
                        stats.getStatLvl(FightStatTypes.HP),
                        stats.getStatLvl(FightStatTypes.SPEED),
                        stats.getStatLvl(FightStatTypes.DAMAGE),
                        stats.getStatLvl(FightStatTypes.EXP)), false);
        embed.addField("Skill points to spend", String.format("%d\nSpend by TODO:", pointsToSpend), false);

        ctx.event().getChannel().sendMessageEmbeds(embed.build()).setActionRows(rows).queue();
    }
}
