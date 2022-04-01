package Commands.Frog;


public class ValidateCommand extends SenderReceiverCommand {

    public ValidateCommand() {
        super("validate", CommandPermission.EVERYONE,
                """
                        Remind anothew usew that dey mattew to u
                        Usage: %{p}vawidate <usew tag>
                        """,
                new String[]{"@sendew wants evewyone to know that dey awe woved and appweciated"},
                new String[]{"@sendew wants @wecipient to know that dey awe woved and appweciated",
                "Henlo @wecipient: @sendew thinks that u'we doing a gweat job",
                "@wecipient: @sendew wants u to know that u make them smiwe",
                "@sendew thinks @wecipient ish wooking fabuwous today"}
        );
    }

}
