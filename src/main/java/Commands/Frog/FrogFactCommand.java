package Commands.Frog;

import Core.Commands.ServerCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FrogFactCommand extends ServerCommand {

    private List<String> allFacts = new ArrayList<>();

    public FrogFactCommand() {
        super("frogfact", CommandPermission.EVERYONE, "Get a random frog fact");
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String[] args) {

        File frogFactList = new File("src/main/resources/frog-facts.txt");

        Random r = new Random();
        if(allFacts.isEmpty()) { //don't read the whole file every time
            try {
                allFacts = Files.readAllLines(frogFactList.toPath());
            } catch (IOException e) {
                allFacts = new ArrayList<>(List.of("It appears I've forgotten all my frog facts!"));
            }
        }

        int factNum = r.nextInt(allFacts.size());
        event.getChannel().sendMessage(allFacts.get(factNum)).queue();
    }
}
