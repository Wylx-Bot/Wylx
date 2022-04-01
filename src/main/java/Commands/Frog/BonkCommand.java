package Commands.Frog;

public class BonkCommand extends SenderReceiverCommand {

    public BonkCommand() {
        super("bonk", CommandPermission.EVERYONE,
                """
                        Bonks anothew usew
                        usage: %{p}bonk <usew tag>
                        """,
                new String[]{"@sender has tried to bonk someone but missed!",
                "@sendew has twied to bonk someone but missed!"},
                new String[]{"@sendew has bonked @wecipient!",
                        "get bonked, @wecipient!"}
                );
    }

}
