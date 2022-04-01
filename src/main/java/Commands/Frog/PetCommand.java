package Commands.Frog;

public class PetCommand extends SenderReceiverCommand {

    public PetCommand() {
        super("pet", CommandPermission.EVERYONE,
                """
                        Pets UwUylx or another user
                        Usage: %{p}pet <user tag>
                        """,
                new String[]{"@sender has twied tuwu pet someone but no owne was thewe!!",
                        "Hey @sender: who wewe uwu twying tuwu pet?"},
                new String[]{"@sender pets @recipient!",
                        "UwU have bewn pet, @recipient!"}
        );
    }

}
