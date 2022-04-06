package Commands.TimeConversion;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;

public class SetTimezone extends ServerCommand {
    public SetTimezone() {
        super("settimezone", CommandPermission.EVERYONE, """
                Set the timezone that you belong to
                Usage: %{p}settimezone <timezone_abbreviation> - set your timezone
                       %{p}settimezone - lists all supported timezones
                """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        // If theres more than 2 args the user did something wrong
        if(ctx.args().length > 2){
            ctx.event().getMessage().reply(getDescription(ctx.prefix())).queue();
            return;
        }

        // One arg we need to tell the user what their options are
        if(ctx.args().length == 1){
            StringBuilder allZones = new StringBuilder();
            for(Timezone zone : Timezone.values()){
                allZones.append(zone.abrv).append(": ").append(zone.name).append("\n");
            }
            ctx.event().getChannel().sendMessage(allZones).queue();
            return;
        }


    }
}
