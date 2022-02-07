package Commands.Frog;

public class BonkCommand extends SenderReceiverCommand {

    public BonkCommand() {
        super("bonk", CommandPermission.EVERYONE,
                """
                        Bonks another user
                        Usage: $bonk <user tag>
                        """,
                "@sender has tried to bonk someone but missed!",
                "@sender has tried to bonk a user that doesn't exist here!",
                "@sender has bonked @receiver!"
                );
    }

}
