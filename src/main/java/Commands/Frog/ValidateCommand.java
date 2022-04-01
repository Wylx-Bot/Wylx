package Commands.Frog;


public class ValidateCommand extends SenderReceiverCommand {

    public ValidateCommand() {
        super("validate", CommandPermission.EVERYONE,
                """
                        Remind anothew usew that dey mattew to u
                        Usage: %{p}vawidate <usew tag>
                        """,
                new String[]{"@sender wants evewyone to know that dey awe woved and appweciated"},
                new String[]{"@sender wants @recipient to know that dey awe woved and appweciated",
                "Henlo @recipient: @sender thwinks that u'we doing a gweat job",
                "@recipient: @sender wants u to know that u make them smiwe",
                "@sender thinks @recipient ish wooking fabuwous today"}
        );
    }

}
