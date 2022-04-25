package Commands.TimeConversion;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Wylx;
import Database.DatabaseManager;
import Database.DiscordUser;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SetTimezone extends ServerCommand {

    public SetTimezone() {
        super("settimezone", CommandPermission.EVERYONE, """
                Set the timezone that you belong to
                Usage: %{p}settimezone <timezone_abbreviation> - set your timezone
                       %{p}settimezone - lists all supported timezones
                """, "timezone");
    }

    @Override
    public void runCommand(CommandContext ctx) {
        // If theres more than 2 args the user did something wrong
        if(ctx.args().length > 2){
            ctx.event().getMessage().reply(getDescription(ctx.prefix())).queue();
            return;
        }

        // One arg we need to tell the user what their options are
        if(ctx.args().length == 1) {
            ctx.event().getChannel().sendMessageEmbeds(optionsEmbed(ctx)).queue();
            return;
        }

        Timezone timezone = Timezone.getTimezone(ctx.args()[1]);
        if(timezone == null) {
            ctx.event().getChannel().sendMessageEmbeds(optionsEmbed(ctx)).queue();
            return;
        }

        DiscordUser dUser = Wylx.getInstance().getDb().getUser(ctx.authorID());
        dUser.setSetting(UserIdentifiers.Timezone, timezone.abrv);
        ctx.event().getChannel().sendMessage("Set Timezone to: " + timezone.abrv).queue();
    }

    private MessageEmbed optionsEmbed(CommandContext ctx){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Wylx Timezones");
        builder.setColor(ctx.event().getGuild().getSelfMember().getColor());
        for(Timezone zone : Timezone.values()){
            builder.appendDescription(zone.abrv + ":    " + zone.name + "\n");
        }
        return builder.build();
    }
}
