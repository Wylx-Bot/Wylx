package Commands.Roles.RolesUtil;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Role;

public record RoleReaction (Role role, Emoji emoji) { }
