package Commands.Roles;

import Core.Events.Commands.CommandContext;
import Core.Events.Commands.ServerCommand;
import Commands.Roles.RolesUtil.RoleMenu;
import Commands.Roles.RolesUtil.RoleReaction;
import Core.Wylx;
import Database.DbElements.DiscordRoleMenu;
import Database.DbElements.RoleMenuIdentifiers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        DiscordRoleMenu roleDb = Wylx.getInstance().getDb().getRoleMenu(ctx.args()[1]);
        RoleMenu menu = roleDb.getSettingOrNull(RoleMenuIdentifiers.ROLE_MENU);
        if (menu == null) {
            ctx.event().getMessage().reply("Could not find menu").queue();
            return;
        }

        // DMs may be blocked, attempt to send DM before adding a listener
        try {
            DMListener listener = new DMListener(menu, user, ctx.event().getGuild());
            ctx.event().getJDA().addEventListener(listener);
        } catch (ErrorResponseException e) {
            ctx.event().getMessage().reply("User private messages are blocked! Make sure that DMs are enabled for this guild.").queue();
        }
    }

    private static class DMListener extends ListenerAdapter {
        private final RoleMenu menu;
        private final User user;
        private final PrivateChannel privateChannel;
        private final Guild guild;
        private Timer timer = new Timer();
        private static final long TIMEOUT = 15 * 60 * 1000; // 15 Minutes

        public DMListener(RoleMenu menu, User user, Guild guild) throws ErrorResponseException {
            this.menu = menu;
            this.user = user;
            this.guild = guild;

            privateChannel = user.openPrivateChannel().complete();
            sendUsageAndMenu(privateChannel);
            resetTimer();
        }

        private void resetTimer() {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    privateChannel.sendMessage("Timed out. Finished editing Role Menu").queue();
                    quit();
                }
            }, TIMEOUT);
        }

        private void quit() {
            timer.cancel();
            user.getJDA().removeEventListener(this);
        }

        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            if (event.getAuthor().getIdLong() != user.getIdLong() ||
                !event.getChannel().equals(privateChannel)) {
                return;
            }

            String[] rawArgs = event.getMessage().getContentRaw().split(" ");
            List<String> filteredArgs = Arrays.stream(rawArgs).filter(arg -> arg.length() != 0).toList();

            String command = filteredArgs.get(0).toLowerCase();
            String options = event.getMessage().getContentRaw().substring(command.length()).trim();

            resetTimer();

            switch (command) {
                case "quit" -> {
                    event.getChannel().sendMessage("Finished editing Role Menu").queue();
                    quit();
                    return;
                }
                case "settitle" -> {
                    if (filteredArgs.size() < 2) {
                        event.getChannel().sendMessage("Please give a title").queue();
                        return;
                    } else {
                        menu.setTitle(options);
                    }
                }
                case "addrole" -> {
                    if (!addRole(filteredArgs, options, event)) {
                        return;
                    }
                }
                case "removerole" -> {
                    try {
                        menu.removeReaction(options.trim());
                    } catch (IllegalArgumentException e) {
                        event.getChannel().sendMessage(e.getMessage()).queue();
                    }
                }
                default -> { return; }
            }

            // Save changes
            Wylx.getInstance().getDb().getRoleMenu(menu.getMessageID())
                    .setSetting(RoleMenuIdentifiers.ROLE_MENU, menu);

            // Display new menu
            try {
                sendUsageAndMenu(event.getChannel());
            } catch (ErrorResponseException e) {
                event.getJDA().removeEventListener(this);
            }
        }

        private boolean addRole(List<String> args, String options, MessageReceivedEvent event) {
            Emoji emoji;
            String roleName;

            if (args.size() <= 1) {
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
                emoji = Emoji.fromMarkdown(args.get(1));
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
            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage(e.getMessage()).queue();
                return false;
            }
        }

        private void sendUsageAndMenu(MessageChannel channel) throws ErrorResponseException {
            channel.sendMessageEmbeds(menu.getEmbed(false)).complete();
            channel.sendMessage("""
                    The above embed is how your menu currently looks.
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
