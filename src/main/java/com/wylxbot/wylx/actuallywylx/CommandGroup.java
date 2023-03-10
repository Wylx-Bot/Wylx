package com.wylxbot.wylx.actuallywylx;

import java.util.List;

public record CommandGroup(String name, String desc, List<WylxCommand> commands) { }
