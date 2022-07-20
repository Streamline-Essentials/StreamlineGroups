package tv.quaint.commands;

import net.streamline.api.command.ModuleCommand;
import net.streamline.api.modules.BundledModule;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.SavableUser;
import net.streamline.base.configs.MainMessagesHandler;
import net.streamline.utils.UUIDUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.parties.SavableParty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PartyCommand extends ModuleCommand {
    private final String useOther;

    public PartyCommand(BundledModule module) {
        super(module,
                "party",
                "streamline.command.party.default",
                "p"
        );

        this.useOther = this.getCommandResource().getOrSetDefault("permissions.use.other", "streamline.command.party.others");
    }

    @Override
    public void run(SavableUser savableUser, String[] strings) {
        if (strings.length < 1) {
            ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
            return;
        }

        String username = strings[0];

        if (username.equals("create") && ! ModuleUtils.getOnlinePlayerNames().contains("create")) {
            GroupManager.createParty(savableUser, savableUser);
            return;
        }
        if (username.equals("list") && ! ModuleUtils.getOnlinePlayerNames().contains("list")) {
            GroupManager.listParty(savableUser, savableUser);
            return;
        }
        if (username.equals("disband") && ! ModuleUtils.getOnlinePlayerNames().contains("disband")) {
            GroupManager.disbandParty(savableUser, savableUser);
            return;
        }

        if (strings.length < 2) {
            ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
            return;
        }

        String action = strings[1].toLowerCase(Locale.ROOT);

        SavableUser other = ModuleUtils.getOrGetUser(UUIDUtils.swapToUUID(username));

        if (other == null) {
            ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.USER_OTHER.get());
            return;
        }

        switch (action) {
            case "create" -> {
                if (! ModuleUtils.hasPermission(savableUser, this.useOther)) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupManager.createParty(savableUser, other);
            }
            case "list" -> {
                if (! ModuleUtils.hasPermission(savableUser, this.useOther)) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }
                
                GroupManager.listParty(savableUser, other);
            }
            case "invite" -> {
                GroupManager.invitePlayerParty(savableUser, savableUser, other);
            }
            case "accept" -> {
                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }
                
                GroupManager.acceptInviteParty(savableUser, other, savableUser);
            }
            case "deny" -> {
                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteParty(savableUser, other, savableUser);
            }
            case "disband" -> {
                if (! ModuleUtils.hasPermission(savableUser, this.useOther)) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableParty party = user.getGroup(SavableParty.class);
                if (party == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandParty(savableUser, party.owner);
            }
        }
    }

    @Override
    public List<String> doTabComplete(SavableUser savableUser, String[] strings) {
        if (strings.length <= 1) {
            List<String> first = new ArrayList<>(ModuleUtils.getOnlinePlayerNames());
            first.add("create");
            first.add("disband");
            first.add("list");
            return first;
        }
        if (strings.length == 2) {
            return List.of(
                    "create",
                    "list",
                    "invite",
                    "accept",
                    "deny",
                    "disband"
            );
        }

        return new ArrayList<>();
    }
}
