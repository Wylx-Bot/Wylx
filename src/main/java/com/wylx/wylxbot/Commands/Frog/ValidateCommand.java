package com.wylx.wylxbot.Commands.Frog;


public class ValidateCommand extends SenderReceiverCommand {

    public ValidateCommand() {
        super("validate", CommandPermission.EVERYONE,
                """
                        Remind another user that they matter to you
                        Usage: %{p}validate <user tag>
                        """,
                new String[]{"@sender wants everyone to know that they are loved and appreciated"},
                new String[]{"@sender wants @recipient to know that they are loved and appreciated",
                "Hey @recipient: @sender thinks that you're doing a great job",
                "@recipient: @sender wants you to know that you make them smile",
                "@sender thinks @recipient is looking fabulous today"}
        );
    }

}
