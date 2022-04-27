package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Core.Roles.RoleMenu;
import Core.Roles.RoleReaction;
import Core.Wylx;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyRoleMenuCommand extends ServerCommand {
    ModifyRoleMenuCommand() {
        super("modifyrolemenu", CommandPermission.DISCORD_PERM, Permission.MANAGE_ROLES,
                """
                        Modify role menu by setting the title, removing roles, and adding roles to a menu.
                        Message ID is shown at the bottom of a role menu.
                        Usage: %{p}modifyrolemenu <message id>
                        """);
    }

    @Override
    public void runCommand(CommandContext ctx) {
        User user = ctx.event().getMessage().getAuthor();

        if (ctx.args().length != 2) {
            ctx.event().getMessage().reply("Please provide a message id").queue();
            return;
        }

        RoleMenu menu = Wylx.getInstance().getDb().getRoleMenu(ctx.args()[1]);
        if (menu == null) {
            ctx.event().getMessage().reply("Could not find menu").queue();
            return;
        }

        // DMs may be blocked, attempt to send DM before adding a listener
        try {
            DMListener listener = new DMListener(menu, user, ctx.event().getGuild());
            ctx.event().getJDA().addEventListener(listener);
        } catch (ErrorResponseException e) {
            ctx.event().getMessage().reply("User private messages are blocked!").queue();
        }
    }

    private static class DMListener extends ListenerAdapter {
        private final RoleMenu menu;
        private final User user;
        private final Guild guild;

        public DMListener(RoleMenu menu, User user, Guild guild) throws ErrorResponseException {
            this.menu = menu;
            this.user = user;
            this.guild = guild;

            PrivateChannel channel = user.openPrivateChannel().complete();
            sendUsageAndMenu(channel);
        }

        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            if (event.getAuthor().getIdLong() != user.getIdLong() ||
                event.isFromGuild()) {
                return;
            }

            String[] args = event.getMessage().getContentRaw().split(" ");
            String options = event.getMessage().getContentRaw().substring(args[0].length()).trim();

            switch (args[0].toLowerCase()) {
                case "quit" -> {
                    event.getChannel().sendMessage("Finished editing Role Menu").queue();
                    user.getJDA().removeEventListener(this);
                    return;
                }
                case "settitle" -> {
                    if (args.length < 2) {
                        event.getChannel().sendMessage("Please give a title").queue();
                        return;
                    } else {
                        menu.setTitle(options);
                    }
                }
                case "addrole" -> {
                    if (!addRole(args, options, event)) {
                        return;
                    }
                }
                case "removerole" -> {
                    try {
                        menu.removeReaction(options.trim());
                    } catch (Exception e) {
                        event.getChannel().sendMessage(e.getMessage()).queue();
                    }
                }
                default -> { return; }
            }

            // Save changes
            Wylx.getInstance().getDb().setRoleMenu(menu);
            // Display new menu
            try {
                sendUsageAndMenu(event.getChannel());
            } catch (ErrorResponseException e) {
                event.getJDA().removeEventListener(this);
            }
        }

        private boolean addRole(String[] args, String options, MessageReceivedEvent event) {
            Emoji emoji;
            String roleName;

            if (args.length <= 1) {
                event.getChannel().sendMessage("No emoji or role provided").queue();
                 return false;
            }

            // Get emote from message
            if (options.startsWith(":")) {
                // Get emote name from :emote name:
                int secondColon = options.indexOf(":", 1);
                if (secondColon == -1) {
                    event.getChannel().sendMessage("Invalid emote").queue();
                    return false;
                }

                // Get emote by name
                List<Emote> emotes = guild.getEmotesByName(options.substring(1, secondColon), true);
                if (emotes.size() == 0) {
                    event.getChannel().sendMessage("Invalid emote").queue();
                    return false;
                }

                emoji = Emoji.fromEmote(emotes.get(0));
                roleName = options.substring(emoji.getName().length() + 2);
            } else {
                // Unicode emote or custom emote
                emoji = Emoji.fromMarkdown(args[1]);
                roleName = options.substring(emoji.getAsMention().length());
            }

            // Get role now
            if (roleName.length() == 0) {
                event.getChannel().sendMessage("No role provided").queue();
                return false;
            }

            List<Role> roles = guild.getRolesByName(roleName.trim(), true);
            if (roles.size() == 0) {
                event.getChannel().sendMessage("Role does not exist").queue();
                return false;
            }

            // Add reaction to menu
            try {
                menu.addReaction(new RoleReaction(roles.get(0), emoji));
                return true;
            } catch (Exception e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
                return false;
            }
        }

        private void sendUsageAndMenu(MessageChannel channel) throws ErrorResponseException {
            channel.sendMessageEmbeds(menu.getEmbed(false)).complete();
            channel.sendMessage("""
                    The below embed is how your menu currently looks.
                    Use the below commands to edit the menu:
                    ```
                    addrole <emoji> <role name> - Add role to menu
                    removerole <role name>      - Remove role from menu
                    settitle <new title>        - Set title for menu
                    quit                        - Stop modifying the menu
                    ```
                    """).complete();
        }
    }
}
