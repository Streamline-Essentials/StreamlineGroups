package host.plas.commands;

import lombok.Getter;
import net.streamline.api.command.ModuleCommand;
import net.streamline.api.configs.given.MainMessagesHandler;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.StreamlineModule;
import net.streamline.api.savables.users.StreamlineUser;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import host.plas.savable.GroupedUser;
import host.plas.savable.flags.GroupFlag;
import host.plas.savable.parties.SavableParty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListSet;

public class PartyCommand extends ModuleCommand {
    @Getter
    private final String useOther;

    public PartyCommand(StreamlineModule module) {
        super(module,
                "party",
                "streamline.command.party.default",
                "p"
        );

        this.useOther = this.getCommandResource().getOrSetDefault("permissions.use.other", "streamline.command.party.others");
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
                if (strings.length > 1) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                GroupManager.createParty(sender, sender);
                break;
            case "list":
                if (strings.length < 2) {
                    GroupManager.listParty(sender, sender);
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

                GroupManager.listParty(sender, other);
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
                    GroupManager.invitePlayerParty(sender, sender, otherInvite);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.invitePlayerParty(sender, otherInvite, otherOther);
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
                    GroupManager.acceptInviteParty(sender, otherAccept, sender);
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
                SavableParty partyAccept = userAccept.getGroup(SavableParty.class);
                if (partyAccept == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.acceptInviteParty(sender, otherAccept, otherOtherAccept);
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
                    GroupManager.denyInviteParty(sender, otherDeny, sender);
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
                SavableParty partyDeny = userDeny.getGroup(SavableParty.class);
                if (partyDeny == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteParty(sender, otherDeny, otherOtherDeny);
                break;
            case "disband":
                if (strings.length < 2) {
                    GroupManager.disbandParty(sender, sender);
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
                SavableParty partyDisband = userDisband.getGroup(SavableParty.class);
                if (partyDisband == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandParty(sender, partyDisband.owner);
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
                    GroupManager.promoteParty(sender, sender, otherPromote);
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
                SavableParty partyPromote = userPromote.getGroup(SavableParty.class);
                if (partyPromote == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.promoteParty(sender, otherPromote, otherOtherPromote);
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
                    GroupManager.demoteParty(sender, sender, otherDemote);
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
                SavableParty partyDemote = userDemote.getGroup(SavableParty.class);
                if (partyDemote == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.demoteParty(sender, otherDemote, otherOtherDemote);
                break;
            case "leave":
                if (strings.length < 2) {
                    GroupManager.leaveParty(sender, sender);
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

                GroupManager.leaveParty(sender, otherLeave);
                break;
            case "chat":
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                String message = ModuleUtils.argsToStringMinus(strings, 0);

                GroupManager.chatParty(sender, sender, message);
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

                GroupManager.chatParty(sender, otherChatAs, messageChatAs);
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

                GroupManager.createParty(sender, otherCreateAs);
                break;
        }
    }

    @Override
    public ConcurrentSkipListSet<String> doTabComplete(StreamlineUser StreamlineUser, String[] strings) {
        if (strings.length <= 1) {
            return new ConcurrentSkipListSet<>(List.of(
                    "create",
                    "create-as",
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
            if (strings[0].equalsIgnoreCase("create-as")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
            if (strings[0].equalsIgnoreCase("list") || strings[0].equalsIgnoreCase("disband")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
            if (strings[0].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUuid());
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) return ModuleUtils.getOnlinePlayerNames();
                if (! party.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ConcurrentSkipListSet<>();
                ConcurrentSkipListSet<StreamlineUser> users = party.getAllUsers();
                party.groupRoleMap.rolesAbove(party.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(party.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(party.groupRoleMap.getUsersOf(party.getRole(StreamlineUser)));
                ConcurrentSkipListSet<String> names = new ConcurrentSkipListSet<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[0].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUuid());
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) return ModuleUtils.getOnlinePlayerNames();
                if (! party.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ConcurrentSkipListSet<>();
                ConcurrentSkipListSet<StreamlineUser> users = party.getAllUsers();
                party.groupRoleMap.rolesAbove(party.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(party.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(party.groupRoleMap.getUsersOf(party.getRole(StreamlineUser)));
                ConcurrentSkipListSet<String> names = new ConcurrentSkipListSet<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }

            if (strings[0].equalsIgnoreCase("accept") || strings[0].equalsIgnoreCase("deny")
                    || strings[0].equalsIgnoreCase("invite")) {
                return ModuleUtils.getOnlinePlayerNames();
            }
        }

        if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("chat-as")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
        }

        return new ConcurrentSkipListSet<>();
    }
}
