package com.wylxbot.wylx.Commands.Roles;

import com.wylxbot.wylx.Core.Events.Commands.ServerCommand;
import com.wylxbot.wylx.Core.Events.EventPackage;
import com.wylxbot.wylx.Core.Events.SilentEvents.SilentEvent;

public class RolePackage extends EventPackage {

    public RolePackage() {
        super(
                new ServerCommand[]{
                        new NewRoleMenuCommand(),
                        new ModifyRoleMenuCommand(),
                }, new SilentEvent[]{}
        );
    }

    @Override
    public String getHeader() {
        return "Have users self-assign roles";
    }
}
