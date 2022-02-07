package Commands.Frog;


public class ValidateCommand extends SenderReceiverCommand {

    public ValidateCommand() {
        super("validate", CommandPermission.EVERYONE,
                """
                        Remind another user that they matter to you
                        Usage: validate <user tag>
                        """,
                "@sender wants everyone to know that they are loved and appreciated.",
                "@sender wants @recipient to know that they are loved and appreciated");
    }

}
