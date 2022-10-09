package com.wylxbot.wylx.Commands.Frog;

public class BonkCommand extends SenderReceiverCommand {

    public BonkCommand() {
        super("bonk", CommandPermission.EVERYONE,
                """
                        Bonks another user
                        Usage: %{p}bonk <user tag>
                        """,
                new String[]{"@sender has tried to bonk someone but missed!",
                "Hey @sender: who were you trying to bonk?"},
                new String[]{"@sender has bonked @recipient!",
                "Get bonked, @recipient!"}
                );
    }

}
