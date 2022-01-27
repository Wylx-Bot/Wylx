package Commands.Management;

import Core.Commands.ThreadedCommand;
import Core.Wylx;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UpdateCommand extends ThreadedCommand {
    private static final String MAIN_BRANCH = "main";

    UpdateCommand() {
        super ("update", CommandPermission.BOT_ADMIN, "Update and build");
    }

    @Override
    public void runCommandThread(MessageReceivedEvent event, String[] args) {
        if (args.length == 2 && !Wylx.getInstance().isRelease()) {
            event.getChannel().sendMessage("Unable to use different branch on RELEASE").queue();
            return;
        }

        String branch = args.length == 2 ? args[1] : MAIN_BRANCH;
        if (!updateGit(event, branch)) return;
        runGradlewBuild(event);
        runGradlewInstall(event);
    }

    private boolean updateGit(MessageReceivedEvent event, String branch) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"git", "pull", "origin", branch};

        try {
            Process proc = rt.exec(commands);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader errInput = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));


            if (proc.waitFor() != 0) {
                event.getChannel().sendMessage("An error occurred while updating.").queue();
                StringBuilder errOutput = new StringBuilder("Error Output:\n```");
                while (errInput.ready()) {
                    errOutput.append(errInput.readLine()).append("\n");
                }
                errOutput.append("\n```");
                event.getChannel().sendMessage(errOutput.toString()).queue();
            }

            StringBuilder stdOutput = new StringBuilder("Stdout:\n```");
            while (stdInput.ready()) {
                stdOutput.append(stdInput.readLine());
            }
            stdOutput.append("\n```");
            event.getChannel().sendMessage(stdOutput.toString()).queue();

            stdInput.close();
            errInput.close();
            return proc.exitValue() == 0;
        } catch (Exception e) {
            event.getChannel().sendMessage("Exception while running git").queue();
            return false;
        }
    }

    private void runGradlewBuild(MessageReceivedEvent event) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"./gradlew", "build"};

        try {
            Process proc = rt.exec(commands);

            if (proc.waitFor() != 0) {
                event.getChannel().sendMessage("An error occurred while building").queue();
            } else {
                event.getChannel().sendMessage("Gradlew built Wylx").queue();
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Exception while running gradlew").queue();
        }
    }

    private void runGradlewInstall(MessageReceivedEvent event) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"./gradlew", "installdist"};

        try {
            Process proc = rt.exec(commands);

            if (proc.waitFor() != 0) {
                event.getChannel().sendMessage("An error occurred while running installDist").queue();
            } else {
                event.getChannel().sendMessage("Gradlew finished running installDist - Ready for restart!").queue();
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Exception while running gradlew").queue();
        }
    }
}
