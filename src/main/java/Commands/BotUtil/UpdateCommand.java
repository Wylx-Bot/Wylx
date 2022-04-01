package Commands.BotUtil;

import Core.Commands.CommandContext;
import Core.Commands.ThreadedCommand;
import Core.Wylx;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UpdateCommand extends ThreadedCommand {
    private static final String MAIN_BRANCH = "main";

    UpdateCommand() {
        super ("update", CommandPermission.BOT_ADMIN, "UwUpdate awnd buildu (*ฅ́˘ฅ̀*)");
    }

    @Override
    public void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        String[] args = ctx.args();
        if (args.length == 2 && !Wylx.getInstance().getWylxConfig().release) {
            event.getChannel().sendMessage("UwUnable to use diffwerent brawnch own RAWRELEASE").queue();
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
                event.getChannel().sendMessage("Awn ewrror owoccurred while uwupdating.").queue();
                StringBuilder errOutput = new StringBuilder("Error Owutpuwt:\n```");
                while (errInput.ready()) {
                    errOutput.append(errInput.readLine()).append("\n");
                }
                errOutput.append("\n```");
                event.getChannel().sendMessage(errOutput.toString()).queue();
            }

            StringBuilder stdOutput = new StringBuilder("Stdout:\n```");
            while (stdInput.ready()) {
                stdOutput.append(stdInput.readLine()).append("\n");
            }
            stdOutput.append("\n```");
            event.getChannel().sendMessage(stdOutput.toString()).queue();

            stdInput.close();
            errInput.close();
            return proc.exitValue() == 0;
        } catch (Exception e) {
            event.getChannel().sendMessage("Ewxceptiowon while rawring giwt").queue();
            return false;
        }
    }

    private void runGradlewBuild(MessageReceivedEvent event) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"./gradlew", "build"};

        try {
            Process proc = rt.exec(commands);

            if (proc.waitFor() != 0) {
                event.getChannel().sendMessage("Awn ewrror owoccurred while buwilding").queue();
            } else {
                event.getChannel().sendMessage("Gradleuwu buwilt UWUylx").queue();
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Exceweption while ruwnning gradleuwu").queue();
        }
    }

    private void runGradlewInstall(MessageReceivedEvent event) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"./gradlew", "installdist"};

        try {
            Process proc = rt.exec(commands);

            if (proc.waitFor() != 0) {
                event.getChannel().sendMessage("An ewwow occuwwed whiwe wunning instawwdist").queue();
            } else {
                event.getChannel().sendMessage("Gwadwew finished wunning instawwdist - weady fow westawt!").queue();
            }

        } catch (Exception e) {
            event.getChannel().sendMessage("Exception whiwe wunning gwadwew").queue();
        }
    }
}
