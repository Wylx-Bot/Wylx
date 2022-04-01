package Commands.Frog;

public class BonkCommand extends SenderReceiverCommand {

    public BonkCommand() {
        super("bonk", CommandPermission.EVERYONE,
                """
                        Bonks anothew usew
                        usage: %{p}bonk <usew tag>
                        """,
                new String[]{"@sender has twied to bowonk someowone but mwissed!",
                "@sender has twied to bowonk someowone but mwissed!"},
                new String[]{"@sender has bowonked @recipient!",
                        "get bowonked, @recipient!"}
                );
    }

}
