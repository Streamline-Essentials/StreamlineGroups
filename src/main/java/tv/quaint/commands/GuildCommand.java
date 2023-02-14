package tv.quaint.commands;

import lombok.Getter;
import net.streamline.api.command.ModuleCommand;
import net.streamline.api.configs.given.MainMessagesHandler;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.StreamlineModule;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.flags.GroupFlag;
import tv.quaint.savable.guilds.SavableGuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListSet;

public class GuildCommand extends ModuleCommand {
    @Getter
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
        if (strings[0].equals("")) {
            ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
            return;
        }

        String action = strings[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "create":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }
                if (strings.length > 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                GroupManager.createGuild(sender, sender, strings[1]);
                break;
            case "rename":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }
                if (strings.length > 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                GroupManager.renameGuild(sender, sender, strings[1]);
                break;
            case "list":
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
                break;
            case "invite":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser otherInvite = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherInvite == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.invitePlayerGuild(sender, sender, otherInvite);
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

                GroupedUser user = GroupManager.getOrGetGroupedUser(otherOther.getUuid());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.invitePlayerGuild(sender, otherInvite, otherOther);
                break;
            case "accept":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser otherAccept = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherAccept == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.acceptInviteGuild(sender, otherAccept, sender);
                    return;
                }

                StreamlineUser otherOtherAccept = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOtherAccept == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser userAccept = GroupManager.getOrGetGroupedUser(otherOtherAccept.getUuid());
                SavableGuild guildAccept = userAccept.getGroup(SavableGuild.class);
                if (guildAccept == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.acceptInviteGuild(sender, otherAccept, otherOtherAccept);
                break;
            case "deny":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser otherDeny = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherDeny == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.denyInviteGuild(sender, otherDeny, sender);
                    return;
                }

                StreamlineUser otherOtherDeny = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOtherDeny == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser userDeny = GroupManager.getOrGetGroupedUser(otherOtherDeny.getUuid());
                SavableGuild guildDeny = userDeny.getGroup(SavableGuild.class);
                if (guildDeny == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteGuild(sender, otherDeny, otherOtherDeny);
                break;
            case "disband":
                if (strings.length < 2) {
                    GroupManager.disbandGuild(sender, sender);
                    return;
                }

                StreamlineUser otherDisband = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherDisband == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser userDisband = GroupManager.getOrGetGroupedUser(otherDisband.getUuid());
                SavableGuild guildDisband = userDisband.getGroup(SavableGuild.class);
                if (guildDisband == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandGuild(sender, guildDisband.owner);
                break;
            case "promote":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser otherPromote = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherPromote == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.promoteGuild(sender, sender, otherPromote);
                    return;
                }

                StreamlineUser otherOtherPromote = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOtherPromote == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser userPromote = GroupManager.getOrGetGroupedUser(otherOtherPromote.getUuid());
                SavableGuild guildPromote = userPromote.getGroup(SavableGuild.class);
                if (guildPromote == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.promoteGuild(sender, otherPromote, otherOtherPromote);
                break;
            case "demote":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                StreamlineUser otherDemote = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherDemote == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.demoteGuild(sender, sender, otherDemote);
                    return;
                }

                StreamlineUser otherOtherDemote = ModuleUtils.getOrGetUserByName(strings[2]);

                if (otherOtherDemote == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser userDemote = GroupManager.getOrGetGroupedUser(otherOtherDemote.getUuid());
                SavableGuild guildDemote = userDemote.getGroup(SavableGuild.class);
                if (guildDemote == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.demoteGuild(sender, otherDemote, otherOtherDemote);
                break;
            case "leave":
                if (strings.length < 2) {
                    GroupManager.leaveGuild(sender, sender);
                    return;
                }

                StreamlineUser otherLeave = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherLeave == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupManager.leaveGuild(sender, otherLeave);
                break;
            case "chat":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                String message = ModuleUtils.argsToStringMinus(strings, 0);

                GroupManager.chatGuild(sender, sender, message);
                break;
            case "chat-as":
                if (strings.length < 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                StreamlineUser otherChatAs = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherChatAs == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                String messageChatAs = ModuleUtils.argsToStringMinus(strings, 0, 1);

                GroupManager.chatGuild(sender, otherChatAs, messageChatAs);
                break;
            case "create-as":
                if (strings.length < 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                if (strings.length > 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                StreamlineUser otherCreateAs = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherCreateAs == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                GroupManager.createGuild(sender, otherCreateAs, strings[2]);
                break;
            case "rename-as":
                if (strings.length < 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                if (strings.length > 3) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                StreamlineUser otherRenameAs = ModuleUtils.getOrGetUserByName(strings[1]);

                if (otherRenameAs == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                GroupManager.renameGuild(sender, otherRenameAs, strings[2]);
                break;
        }
    }

    @Override
    public ConcurrentSkipListSet<String> doTabComplete(StreamlineUser StreamlineUser, String[] strings) {
        if (strings.length <= 1) {
            return new ConcurrentSkipListSet<>(List.of(
                    "create",
                    "create-as",
                    "rename",
                    "rename-as",
                    "list",
                    "invite",
                    "accept",
                    "deny",
                    "disband",
                    "promote",
                    "demote",
                    "leave",
                    "chat",
                    "chat-as"
            ));
        }
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("create") || strings[0].equalsIgnoreCase("create-as")) {
                return new ConcurrentSkipListSet<>(List.of("<name>"));
            }
            if (strings[0].equalsIgnoreCase("rename") || strings[0].equalsIgnoreCase("rename-as")) {
                return new ConcurrentSkipListSet<>(List.of("<name>"));
            }
            if (strings[0].equalsIgnoreCase("list") || strings[0].equalsIgnoreCase("disband")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
            if (strings[0].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUuid());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ConcurrentSkipListSet<>();
                ConcurrentSkipListSet<StreamlineUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(StreamlineUser)));
                ConcurrentSkipListSet<String> names = new ConcurrentSkipListSet<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[0].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUuid());
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ConcurrentSkipListSet<>();
                ConcurrentSkipListSet<StreamlineUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(StreamlineUser)));
                ConcurrentSkipListSet<String> names = new ConcurrentSkipListSet<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
        }

        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("promote") || strings[0].equalsIgnoreCase("demote")
                    || strings[0].equalsIgnoreCase("accept") || strings[0].equalsIgnoreCase("deny")
                    || strings[0].equalsIgnoreCase("invite") || strings[0].equalsIgnoreCase("create-as")
                    || strings[0].equalsIgnoreCase("rename-as") || strings[0].equalsIgnoreCase("chat-as")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
        }

        return new ConcurrentSkipListSet<>();
    }
}
