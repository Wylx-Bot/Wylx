package com.wylxbot.wylx.actuallywylx;

import com.wylxbot.wylx.actuallywylx.music.*;
import com.wylxbot.wylx.actuallywylx.notmusic.*;

import java.util.List;

public class CommandsContainer {
    public static CommandGroup[] Commands = {
        new CommandGroup("Music", "Have Wylx play music for you!", List.of(
            new InviteCommand(),
            new ActuallyBonkCommand(),
            new SuperSecretCommand(),
            new RolesSuperCommand(),
            new FightCommand()
        )),
        new CommandGroup("Not Music", "Don't have Wylx play music for you :(", List.of(
            new ActuallyPingCommand(),
            new WaitComand(),
            new ModalCommand(),
            new LaTexCommand(),
            new Spell()
        )),
    };
}
