package tv.quaint.commands;

import net.streamline.api.command.ModuleCommand;
import net.streamline.api.configs.given.MainMessagesHandler;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.StreamlineModule;
import net.streamline.api.savables.users.StreamlineUser;
import net.streamline.api.utils.UUIDUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.flags.GroupFlag;
import tv.quaint.savable.guilds.SavableGuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuildCommand extends ModuleCommand {
    private final String useOther;

    public GuildCommand(StreamlineModule module) {
        super(module,
                "guild",
                "streamline.command.guild.default",
                "g"
        );

        this.useOther = this.getCommandResource().getOrSetDefault("permissions.use.other", "streamline.command.guild.others");
    }

    @Override
    public void run(StreamlineUser sender, String[] strings) {
        if (strings.length < 1) {
            ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
            return;
        }

        String action = strings[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "create" -> {
                if (strings.length == 2) {
                    GroupManager.createGuild(sender, sender, strings[1]);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[2]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                if (strings.length > 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                String name = ModuleUtils.argsToStringMinus(strings, 0, 1);

                GroupManager.createGuild(sender, other, name);
            }
            case "rename" -> {
                if (strings.length == 2) {
                    GroupManager.renameGuild(sender, sender, strings[1]);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[2]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                if (strings.length > 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                String name = ModuleUtils.argsToStringMinus(strings, 0, 1);

                GroupManager.renameGuild(sender, other, name);
            }
            case "list" -> {
                if (strings.length < 2) {
                    GroupManager.listGuild(sender, sender);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupManager.listGuild(sender, other);
            }
            case "invite" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.invitePlayerGuild(sender, sender, other);
                    return;
                }

                StreamlineUser otherOther = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOther == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.invitePlayerGuild(sender, other, otherOther);
            }
            case "accept" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.acceptInviteGuild(sender, other, sender);
                    return;
                }

                StreamlineUser otherOther = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOther == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.acceptInviteGuild(sender, other, otherOther);
            }
            case "deny" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.denyInviteGuild(sender, other, sender);
                    return;
                }

                StreamlineUser otherOther = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOther == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteGuild(sender, other, otherOther);
            }
            case "disband" -> {
                if (strings.length < 2) {
                    GroupManager.disbandGuild(sender, sender);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(other.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandGuild(sender, guild.owner);
            }
            case "promote" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.promoteGuild(sender, sender, other);
                    return;
                }

                StreamlineUser otherOther = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOther == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.promoteGuild(sender, other, otherOther);
            }
            case "demote" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.demoteGuild(sender, sender, other);
                    return;
                }

                StreamlineUser otherOther = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOther == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.demoteGuild(sender, other, otherOther);
            }
            case "leave" -> {
                if (strings.length < 2) {
                    GroupManager.leaveGuild(sender, sender);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupManager.leaveGuild(sender, other);
            }
            case "chat" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.chatGuild(sender, sender, ModuleUtils.argsToStringMinus(strings, 0));
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUserByName(strings[1]);

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                String message = ModuleUtils.argsToStringMinus(strings, 0, 1);

                GroupManager.chatGuild(sender, other, message);
            }
        }
    }

    @Override
    public List<String> doTabComplete(StreamlineUser StreamlineUser, String[] strings) {
        if (strings.length <= 1) {
            return List.of(
                    "create",
                    "list",
                    "invite",
                    "accept",
                    "deny",
                    "disband",
                    "promote",
                    "demote",
                    "leave",
                    "chat",
                    "rename"
            );
        }
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("create")) {
                return List.of("<name>");
            }
            if (strings[0].equalsIgnoreCase("rename")) {
                return List.of("<name>");
            }
            if (strings[0].equalsIgnoreCase("list") || strings[0].equalsIgnoreCase("disband")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
            if (strings[0].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<StreamlineUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(StreamlineUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[0].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUUID());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<StreamlineUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(StreamlineUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
        }

        if (strings.length == 3) {
            if (strings[1].equalsIgnoreCase("promote") || strings[1].equalsIgnoreCase("demote")
                    || strings[1].equalsIgnoreCase("accept") || strings[1].equalsIgnoreCase("deny")
                    || strings[1].equalsIgnoreCase("invite") || strings[1].equalsIgnoreCase("create")
                    || strings[1].equalsIgnoreCase("rename")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
        }

        return new ArrayList<>();
    }
}
