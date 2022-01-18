package Commands.Management;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;


public class SystemCommand extends ServerCommand {
    private final String commitID = getCommitID();

    private static final long BYTES_PER_MEGABYTE = 1024 * 1024;
    private static final long MILLI_PER_SECOND = 1000;
    private static final long MILLI_PER_MINUTES = MILLI_PER_SECOND * 60;
    private static final long MILLI_PER_HOURS = MILLI_PER_MINUTES * 60;
    private static final long MILLI_PER_DAYS = MILLI_PER_HOURS * 24;

    public SystemCommand() {
        super("system", CommandPermission.EVERYONE);
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

            String commit = stdInput.readLine().trim();
            stdInput.close();
            return commit;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private long milliToDays(long val) {
        return val / MILLI_PER_DAYS;
    }

    private long milliToHours(long val) {
        return val / MILLI_PER_HOURS;
    }

    private long milliToMinutes(long val) {
        return val / MILLI_PER_MINUTES;
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
        var maxMem = rt.maxMemory() / BYTES_PER_MEGABYTE;
        var usedMem = (rt.totalMemory() - rt.freeMemory()) / BYTES_PER_MEGABYTE;
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
                String.format("**Memory**: %dMB of %dMB\n", usedMem, maxMem) +
                progress + "\n\n" +
                String.format("**Bot Uptime**: %d Days, %d Hours, and %d minutes",
                        milliToDays(uptime), milliToHours(uptime), milliToMinutes(uptime));

        embed.setDescription(builder);
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
