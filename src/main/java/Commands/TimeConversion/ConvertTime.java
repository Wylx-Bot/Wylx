package Commands.TimeConversion;

import Core.Events.SilentEvents.SilentEvent;
import Core.Wylx;
import Database.DiscordUser;
import Database.UserIdentifiers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;
import java.util.regex.Pattern;

public class ConvertTime extends SilentEvent {

    private static final String TWELVE_HOUR_REGEX = "(^|(.* ))((1[0-2]|0?[1-9])(:[0-5][0-9])?) *[aApP][mM]($| ).*";

    public ConvertTime(){
        super("Automagically detects messages containing a time and converts them to timezones for server users");
    }

    @Override
    public boolean check(MessageReceivedEvent event, String prefix) {
        // Figure it out for yourself, I did it once but forgot by the time I went to comment it
        return Pattern.matches(TWELVE_HOUR_REGEX, event.getMessage().getContentRaw());
    }

    // TODO: Only convert to the timezones that people in the server have
    @Override
    public void runEvent(MessageReceivedEvent event, String prefix) {
        DiscordUser dUser = Wylx.getInstance().getDb().getUser(event.getAuthor().getId());
        Timezone userTimezone = Timezone.getTimezone(dUser.getSetting(UserIdentifiers.Timezone));

        // Cant do conversions if we don't know the user's timezone
        if(userTimezone == null){
            // Don't prompt a user more than once (spam)
            Boolean timezonePrompted = dUser.getSetting(UserIdentifiers.TimezonePrompted);
            if(timezonePrompted) return;
            // Set it so the user doesn't get prompted multiple times
            dUser.setSetting(UserIdentifiers.TimezonePrompted, true);

            event.getMessage().reply("Please set your timezone using " + prefix + "settimezone <timezone>").queue();
            return;
        }



        event.getChannel().sendMessage("time").queue();
    }
}
