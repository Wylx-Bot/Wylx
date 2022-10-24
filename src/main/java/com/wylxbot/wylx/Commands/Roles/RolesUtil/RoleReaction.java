package com.wylxbot.wylx.Commands.Roles.RolesUtil;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

public record RoleReaction (Role role, EmojiUnion emoji) { }
