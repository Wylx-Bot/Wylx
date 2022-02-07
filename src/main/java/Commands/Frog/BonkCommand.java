package Commands.Frog;

public class BonkCommand extends SenderReceiverCommand {

    public BonkCommand() {
        super("bonk", CommandPermission.EVERYONE,
                """
                        Bonks another user
                        Usage: bonk <user tag>
                        """,
                "@sender has tried to bonk someone but missed!",
                "@sender has bonked @recipient!"
                );
    }

}
