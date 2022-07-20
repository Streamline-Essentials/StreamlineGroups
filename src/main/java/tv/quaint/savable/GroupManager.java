package tv.quaint.savable;

import de.leonhard.storage.Config;
import de.leonhard.storage.Json;
import de.leonhard.storage.Toml;
import lombok.Getter;
import net.streamline.api.configs.*;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.SavableUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;
import tv.quaint.savable.guilds.CreateGuildEvent;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.CreatePartyEvent;
import tv.quaint.savable.parties.SavableParty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class GroupManager {
    @Getter
    private static TreeMap<String, Class<? extends SavableGroup>> registeredClasses = new TreeMap<>();
    @Getter
    private static ConcurrentHashMap<Class<? extends SavableGroup>, Consumer<String>> registeredLoadOrders = new ConcurrentHashMap<>();

    public static void registerClass(Class<? extends SavableGroup> clazz, Consumer<String> consumer) {
        getRegisteredClasses().put(clazz.getSimpleName(), clazz);
        getRegisteredLoadOrders().put(clazz, consumer);
    }

    public static Class<? extends SavableGroup> getRegisteredClass(String name) {
        return getRegisteredClasses().get(name);
    }

    @Getter
    private static List<GroupedUser> loadedGroupedUsers = new ArrayList<>();

    public static void loadGroupedUser(GroupedUser user) {
        loadedGroupedUsers.add(user);
    }

    public static void unloadGroupedUser(GroupedUser user) {
        loadedGroupedUsers.remove(user);
    }

    private static GroupedUser getGroupedUser(String uuid) {
        for (GroupedUser user : loadedGroupedUsers) {
            if (user.uuid.equals(uuid)) return user;
        }
        return null;
    }

    public static GroupedUser getOrGetGroupedUser(String uuid) {
        GroupedUser user = getGroupedUser(uuid);
        if (user != null) return user;

        return new GroupedUser(uuid);
    }
    @Getter
    private static ConcurrentHashMap<Class<? extends SavableGroup>, List<SavableGroup>> loadedGroups = new ConcurrentHashMap<>();

    public static void loadGroup(SavableGroup group) {
        List<SavableGroup> groups = getGroupsOf(group.getClass());
        for (SavableGroup g : groups) {
            if (g.uuid.equals(group.uuid)) return;
        }
        groups.add(group);
        getLoadedGroups().put(group.getClass(), groups);
    }

    public static List<SavableGroup> getGroupsOf(Class<? extends SavableGroup> clazz) {
        List<SavableGroup> groups = getLoadedGroups().get(clazz);
        if (groups == null) groups = new ArrayList<>();
        getLoadedGroups().put(clazz, groups);
        return groups;
    }

    public static void addGroupOf(Class<? extends SavableGroup> clazz, SavableGroup group) {
        getGroupsOf(clazz).add(group);
    }

    public static void removeGroupOf(SavableGroup group) {
        getGroupsOf(group.getClass()).remove(group);
    }

    public static <T extends SavableGroup> T getGroupOfUser(Class<T> clazz, SavableUser user) {
        for (SavableGroup group : getGroupsOf(clazz)) {
            if (group.hasMember(user)) return (T) group;
        }
        return null;
    }

    public static <T extends SavableGroup> T getGroupOfUser(Class<T> clazz, String userUUID) {
        return getGroupOfUser(clazz, ModuleUtils.getOrGetUser(userUUID));
    }
    public static <T extends SavableGroup> T getGroup(Class<T> clazz, String uuid) {
        for (SavableGroup group : getGroupsOf(clazz)) {
            if (group.uuid.equals(uuid)) return (T) group;
        }
        return null;
    }

    public static void removeGroupOf(Class<? extends SavableGroup> clazz, String uuid) {
        SavableGroup group = getGroup(clazz, uuid);
        if (group == null) return;
        removeGroupOf(group);
    }

    public static boolean hasGroup(SavableUser user, Class<? extends SavableGroup> clazz) {
        return getGroupOfUser(clazz, user) != null;
    }

    public static boolean exists(String uuid, Class<? extends SavableGroup> clazz) {
        StorageResource<?> resource = newStorageResource(uuid, clazz);
        if (resource == null) return false;
        return resource.exists();
    }

    public static File groupFolder(Class<? extends SavableResource> clazz) {
        File folder = new File(StreamlineGroups.getGroupsFolder(), clazz.getSimpleName() + File.separator);
        folder.mkdirs();
        return folder;
    }

    public static StorageResource<?> newStorageResource(String uuid, Class<? extends SavableResource> clazz) {
        switch (StreamlineGroups.getConfigs().savingUse()) {
            case YAML -> {
                return new FlatFileResource<>(Config.class, uuid + ".yml", groupFolder(clazz), false);
            }
            case JSON -> {
                return new FlatFileResource<>(Json.class, uuid + ".json", groupFolder(clazz), false);
            }
            case TOML -> {
                return new FlatFileResource<>(Toml.class, uuid + ".toml", groupFolder(clazz), false);
            }
            case MONGO -> {
                return new MongoResource(StreamlineGroups.getConfigs().getConfiguredDatabase(), clazz.getSimpleName(), "uuid", uuid);
            }
            case MYSQL -> {
                return new MySQLResource(StreamlineGroups.getConfigs().getConfiguredDatabase(), new SQLCollection(clazz.getSimpleName(), "uuid", uuid));
            }
        }

        return null;
    }

    public static StorageResource<?> newStorageResourceUsers(String uuid, Class<? extends SavableResource> clazz) {
        switch (StreamlineGroups.getConfigs().savingUse()) {
            case YAML -> {
                return new FlatFileResource<>(Config.class, uuid + ".yml", StreamlineGroups.getUsersFolder(), false);
            }
            case JSON -> {
                return new FlatFileResource<>(Json.class, uuid + ".json", StreamlineGroups.getUsersFolder(), false);
            }
            case TOML -> {
                return new FlatFileResource<>(Toml.class, uuid + ".toml", StreamlineGroups.getUsersFolder(), false);
            }
            case MONGO -> {
                return new MongoResource(StreamlineGroups.getConfigs().getConfiguredDatabase(), clazz.getSimpleName(), "uuid", uuid);
            }
            case MYSQL -> {
                return new MySQLResource(StreamlineGroups.getConfigs().getConfiguredDatabase(), new SQLCollection(clazz.getSimpleName(), "uuid", uuid));
            }
        }

        return null;
    }

    public static SavableGuild getOrGetGuild(String uuid) {
        SavableGuild guild = getGroup(SavableGuild.class, uuid);
        if (guild != null) return guild;

        if (exists(uuid, SavableGuild.class)) {
            guild = new SavableGuild(uuid);

            loadGroup(guild);
            return guild;
        } else {
            return null;
        }
    }

    public static SavableParty getOrGetParty(String uuid) {
        SavableParty party = getGroup(SavableParty.class, uuid);
        if (party != null) return party;

        if (exists(uuid, SavableParty.class)) {
            party = new SavableParty(uuid);

            loadGroup(party);
            return party;
        } else {
            return null;
        }
    }

    public static SavableGuild createGuild(SavableUser sender, SavableUser leader, String name) {
        if (hasGroup(leader, SavableGuild.class)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyExists());
            return getGroupOfUser(SavableGuild.class, leader);
        }

        SavableGuild guild = new SavableGuild(leader.uuid);
        guild.name = name;
        guild.saveAll();

        ModuleUtils.sendMessage(leader, StreamlineGroups.getMessages().guildsCreate());
        if (sender != leader) ModuleUtils.sendMessage(sender, leader, StreamlineGroups.getMessages().guildsCreate());

        ModuleUtils.fireEvent(new CreateGuildEvent(guild, leader));

        return guild;
    }

    public static SavableParty createParty(SavableUser sender, SavableUser leader) {
        if (hasGroup(leader, SavableParty.class)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyExists());
            return getGroupOfUser(SavableParty.class, leader);
        }

        SavableParty party = new SavableParty(leader.uuid);
        party.saveAll();

        ModuleUtils.sendMessage(leader, StreamlineGroups.getMessages().partiesCreate());
        if (sender != leader) ModuleUtils.sendMessage(sender, leader, StreamlineGroups.getMessages().partiesCreate());

        ModuleUtils.fireEvent(new CreatePartyEvent(party, leader));

        return party;
    }

    public static void invitePlayerGuild(SavableUser sender, SavableUser other, SavableUser toInvite) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser user = getOrGetGroupedUser(toInvite.uuid);
        if (user.getGroup(SavableGuild.class) != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInOther());
            return;
        }

        guild.addInvite(sender, toInvite);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().guildsSendInviteSender()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(toInvite, StreamlineGroups.getMessages().guildsSendInviteOther()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsSendInviteMembers()
                    .replace("%this_other%", toInvite.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        });
    }

    public static void invitePlayerParty(SavableUser sender, SavableUser other, SavableUser toInvite) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser user = getOrGetGroupedUser(toInvite.uuid);
        if (user.getGroup(SavableParty.class) != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInOther());
            return;
        }

        party.addInvite(sender, toInvite);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().partiesSendInviteSender()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        ModuleUtils.sendMessage(toInvite, StreamlineGroups.getMessages().partiesSendInviteOther()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesSendInviteMembers()
                    .replace("%this_other%", toInvite.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        });
    }

    public static void acceptInviteGuild(SavableUser sender, SavableUser other, SavableUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser groupedInvited = getOrGetGroupedUser(invited.uuid);
        SavableGuild already = groupedInvited.getGroup(SavableGuild.class);

        if (already != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInSelf());
            return;
        }

        if (! guild.hasInvite(invited)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInvited());
            return;
        }

        guild.remFromInvites(invited);
        guild.addMember(invited);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().guildsAcceptSender()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(other, StreamlineGroups.getMessages().guildsAcceptOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsAcceptMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        });
    }

    public static void acceptInviteParty(SavableUser sender, SavableUser other, SavableUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser groupedInvited = getOrGetGroupedUser(invited.uuid);
        SavableParty already = groupedInvited.getGroup(SavableParty.class);

        if (already != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInSelf());
            return;
        }

        if (! party.hasInvite(invited)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInvited());
            return;
        }

        party.remFromInvites(invited);
        party.addMember(invited);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().partiesAcceptSender()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        ModuleUtils.sendMessage(other, StreamlineGroups.getMessages().partiesAcceptOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesAcceptMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        });
    }

    public static void denyInviteGuild(SavableUser sender, SavableUser other, SavableUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasInvite(invited)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInvited());
            return;
        }

        guild.remFromInvites(invited);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().guildsDenySender()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().guildsDenyOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsDenyMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        });
    }

    public static void denyInviteParty(SavableUser sender, SavableUser other, SavableUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasInvite(invited)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInvited());
            return;
        }

        party.remFromInvites(invited);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().partiesDenySender()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().partiesDenyOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_leader%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesDenyMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        });
    }

    public static void listGuild(SavableUser sender, SavableUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        guild.groupRoleMap.getRoles().forEach(a -> {
            StringBuilder builder = new StringBuilder(a.getName() + "&8: ");

            List<String> formattedNames = new ArrayList<>();
            guild.groupRoleMap.getUsersOf(a).forEach(act -> formattedNames.add(ModuleUtils.getFormatted(act)));

            builder.append(ModuleUtils.getListAsFormattedString(formattedNames));

            ModuleUtils.sendMessage(sender, builder.toString());
        });
    }

    public static void listParty(SavableUser sender, SavableUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        party.groupRoleMap.getRoles().forEach(a -> {
            StringBuilder builder = new StringBuilder(a.getName() + "&8: ");

            List<String> formattedNames = new ArrayList<>();
            party.groupRoleMap.getUsersOf(a).forEach(act -> formattedNames.add(ModuleUtils.getFormatted(act)));

            builder.append(ModuleUtils.getListAsFormattedString(formattedNames));

            ModuleUtils.sendMessage(sender, builder.toString());
        });
    }

    public static void disbandGuild(SavableUser sender, SavableUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (SavableUser user : guild.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableGuild.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(guild.owner)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandLeader()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        }

        guild.disband();
    }

    public static void disbandParty(SavableUser sender, SavableUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (SavableUser user : party.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableParty.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(party.owner)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandLeader()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        }

        party.disband();
    }

    public static void promoteGuild(SavableUser sender, SavableUser other, SavableUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (guild.hasMember(sender)) {
            if (! guild.userHasFlag(sender, GroupFlag.PROMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.PROMOTE));
                return;
            }
        }

        guild.promoteUser(promote);

        for (SavableUser user : guild.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableGuild.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        }
    }

    public static void promoteParty(SavableUser sender, SavableUser other, SavableUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (party.hasMember(sender)) {
            if (! party.userHasFlag(sender, GroupFlag.PROMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.PROMOTE));
                return;
            }
        }

        party.promoteUser(promote);

        for (SavableUser user : party.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableGuild.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        }
    }

    public static void demoteGuild(SavableUser sender, SavableUser other, SavableUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (guild.hasMember(sender)) {
            if (! guild.userHasFlag(sender, GroupFlag.DEMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.DEMOTE));
                return;
            }
        }

        guild.demoteUser(promote);

        for (SavableUser user : guild.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableGuild.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_leader%", guild.owner.getName())
            );
        }
    }

    public static void demoteParty(SavableUser sender, SavableUser other, SavableUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.uuid);
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (party.hasMember(sender)) {
            if (! party.userHasFlag(sender, GroupFlag.DEMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.DEMOTE));
                return;
            }
        }

        party.demoteUser(promote);

        for (SavableUser user : party.getAllUsers()) {
            GroupedUser gu = getOrGetGroupedUser(user.uuid);
            gu.disassociateWith(SavableGuild.class);

            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_leader%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_leader%", party.owner.getName())
            );
        }
    }
}
