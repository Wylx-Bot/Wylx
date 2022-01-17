package Commands.Management;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SystemCommand extends ServerCommand {
    private final String commitID = getCommitID();

    public SystemCommand(String keyword) {
        super(keyword);
    }

    private String getCommitID() {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"git", "rev-parse", "--short", "HEAD"};
        try {
            Process proc = rt.exec(commands);

            if (proc.waitFor() != 0) {
                return "Unknown";
            }

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            return (stdInput.readLine().trim());
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private long byteToMb(long val) {
        return val / 1024 / 1024;
    }

    private long milliToDays(long val) {
        return val / 1000 / 60 / 60 / 24;
    }

    private long milliToHours(long val) {
        return val / 1000 / 60 / 60;
    }

    private long milliToMinutes(long val) {
        return val / 1000 / 60;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(event.getGuild().getSelfMember().getColor());

        try {
            embed.setTitle(String.format("System: %s", InetAddress.getLocalHost().getHostName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime rt = Runtime.getRuntime();
        var maxMem = rt.maxMemory();
        var usedMem = rt.totalMemory() - rt.freeMemory();
        var ratio = (double) usedMem / maxMem;
        var uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        StringBuilder progress = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            progress.append(((double) i / 30) < ratio ? "\u2588" : "\\_");
        }

        String builder =
                String.format("**OS**: %s\n", System.getProperty("os.name")) +
                String.format("**Commit**: %s\n", commitID) +
                String.format("**Threads**: %d\n", rt.availableProcessors()) +
                String.format("**Memory**: %dMB of %dMB\n", byteToMb(usedMem), byteToMb(maxMem)) +
                progress + "\n\n" +
                String.format("**Bot Uptime**: %d Days, %d Hours, and %d minutes",
                        milliToDays(uptime), milliToHours(uptime), milliToMinutes(uptime));

        embed.setDescription(builder);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
