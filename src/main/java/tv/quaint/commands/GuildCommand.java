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
import tv.quaint.savable.flags.GroupFlag;
import tv.quaint.savable.guilds.SavableGuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuildCommand extends ModuleCommand {
    private final String useOther;

    public GuildCommand(BundledModule module) {
        super(module,
                "guild",
                "streamline.command.guild.default",
                "g"
        );

        this.useOther = this.getCommandResource().getOrSetDefault("permissions.use.other", "streamline.command.guild.others");
    }

    @Override
    public void run(SavableUser savableUser, String[] strings) {
        if (strings.length < 1) {
            ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
            return;
        }

        String username = strings[0];

        if (username.equals("create") && ! ModuleUtils.getOnlinePlayerNames().contains("create")) {
            String name = ModuleUtils.argsToStringMinus(strings, 0);
            GroupManager.createGuild(savableUser, savableUser, name);
            return;
        }
        if (username.equals("list") && ! ModuleUtils.getOnlinePlayerNames().contains("list")) {
            GroupManager.listGuild(savableUser, savableUser);
            return;
        }
        if (username.equals("disband") && ! ModuleUtils.getOnlinePlayerNames().contains("disband")) {
            GroupManager.disbandGuild(savableUser, savableUser);
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

                if (strings.length < 3) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.ARGUMENTS_TOO_FEW.get());
                    return;
                }

                String name = ModuleUtils.argsToStringMinus(strings, 0, 1);
                GroupManager.createGuild(savableUser, other, name);
            }
            case "list" -> {
                if (! ModuleUtils.hasPermission(savableUser, this.useOther)) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupManager.listGuild(savableUser, other);
            }
            case "invite" -> {
                GroupManager.invitePlayerGuild(savableUser, savableUser, other);
            }
            case "accept" -> {
                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.acceptInviteGuild(savableUser, other, savableUser);
            }
            case "deny" -> {
                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.denyInviteGuild(savableUser, other, savableUser);
            }
            case "disband" -> {
                if (! ModuleUtils.hasPermission(savableUser, this.useOther)) {
                    ModuleUtils.sendMessage(savableUser, MainMessagesHandler.MESSAGES.INVALID.PERMISSIONS.get());
                    return;
                }

                GroupedUser user = GroupManager.getOrGetGroupedUser(other.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) {
                    ModuleUtils.sendMessage(savableUser, StreamlineGroups.getMessages().errorsBaseNotInOther());
                    return;
                }

                GroupManager.disbandGuild(savableUser, guild.owner);
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
            first.add("promote");
            first.add("demote");
            return first;
        }
        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("create")) {
                return List.of("<name>");
            }
            if (strings[0].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(savableUser.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(savableUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<SavableUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(savableUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(savableUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[0].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(savableUser.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(savableUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<SavableUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(savableUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(savableUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            return List.of(
                    "create",
                    "list",
                    "invite",
                    "accept",
                    "deny",
                    "disband",
                    "promote",
                    "demote"
            );
        }

        if (strings.length == 3) {
            if (strings[1].equalsIgnoreCase("promote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(savableUser.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(savableUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<SavableUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(savableUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(savableUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
            if (strings[1].equalsIgnoreCase("demote")) {
                GroupedUser user = GroupManager.getOrGetGroupedUser(savableUser.uuid);
                SavableGuild guild = user.getGroup(SavableGuild.class);
                if (guild == null) return ModuleUtils.getOnlinePlayerNames();
                if (! guild.userHasFlag(savableUser, GroupFlag.PROMOTE)) return new ArrayList<>();
                List<SavableUser> users = guild.getAllUsers();
                guild.groupRoleMap.rolesAbove(guild.getRole(savableUser)).forEach(a -> {
                    users.removeAll(guild.groupRoleMap.getUsersOf(a));
                });
                users.removeAll(guild.groupRoleMap.getUsersOf(guild.getRole(savableUser)));
                List<String> names = new ArrayList<>();
                users.forEach(a -> {
                    names.add(a.getName());
                });
                return names;
            }
        }

        return new ArrayList<>();
    }
}
