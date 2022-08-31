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
import tv.quaint.savable.parties.SavableParty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PartyCommand extends ModuleCommand {
    private final String useOther;

    public PartyCommand(StreamlineModule module) {
        super(module,
                "party",
                "streamline.command.party.default",
                "g"
        );

        this.useOther = this.getCommandResource().getOrSetDefault("permissions.use.other", "streamline.command.party.others");
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
                if (strings.length == 1) {
                    GroupManager.createParty(sender, sender);
                    return;
                }

                StreamlineUser other = ModuleUtils.getOrGetUser(UUIDUtils.swapToUUID(strings[0]));

                if (other == null) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
                    return;
                }

                if (! ModuleUtils.hasPermission(sender, this.useOther)) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                if (strings.length > 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_MANY.get());
                    return;
                }

                GroupManager.createParty(sender, other);
            }
            case "list" -> {
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
                    GroupManager.invitePlayerParty(sender, sender, other);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.invitePlayerParty(sender, other, otherOther);
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
                    GroupManager.acceptInviteParty(sender, other, sender);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.acceptInviteParty(sender, other, otherOther);
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
                    GroupManager.denyInviteParty(sender, other, sender);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteParty(sender, other, otherOther);
            }
            case "disband" -> {
                if (strings.length < 2) {
                    GroupManager.disbandParty(sender, sender);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandParty(sender, party.owner);
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
                    GroupManager.promoteParty(sender, sender, other);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.promoteParty(sender, other, otherOther);
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
                    GroupManager.demoteParty(sender, sender, other);
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
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.demoteParty(sender, other, otherOther);
            }
            case "leave" -> {
                if (strings.length < 2) {
                    GroupManager.leaveParty(sender, sender);
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

                GroupManager.leaveParty(sender, other);
            }
            case "chat" -> {
                if (strings.length < 2) {
                    ModuleUtils.sendMessage(sender, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                if (strings.length == 2) {
                    GroupManager.chatParty(sender, sender, ModuleUtils.argsToStringMinus(strings, 0));
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

                GroupManager.chatParty(sender, other, message);
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
            if (strings[0].equalsIgnoreCase("create") || strings[0].equalsIgnoreCase("list") || strings[0].equalsIgnoreCase("disband")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
            if (strings[0].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUUID());
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) return ModuleUtils.getOnlinePlayerNames();
                if (! party.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<StreamlineUser> users = party.getAllUsers();
                party.groupRoleMap.rolesAbove(party.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(party.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(party.groupRoleMap.getUsersOf(party.getRole(StreamlineUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[0].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(StreamlineUser.getUUID());
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) return ModuleUtils.getOnlinePlayerNames();
                if (! party.userHasFlag(StreamlineUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<StreamlineUser> users = party.getAllUsers();
                party.groupRoleMap.rolesAbove(party.getRole(StreamlineUser)).forEach(a -> {
                    users.removeAll(party.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(party.groupRoleMap.getUsersOf(party.getRole(StreamlineUser)));
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
                    || strings[1].equalsIgnoreCase("invite")) {
                if (ModuleUtils.hasPermission(StreamlineUser, this.useOther)) {
                    return ModuleUtils.getOnlinePlayerNames();
                }
            }
        }

        return new ArrayList<>();
    }
}
