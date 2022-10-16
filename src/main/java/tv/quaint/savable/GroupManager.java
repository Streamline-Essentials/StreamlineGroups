package tv.quaint.savable;

import de.leonhard.storage.Config;
import de.leonhard.storage.Json;
import de.leonhard.storage.Toml;
import lombok.Getter;
import lombok.Setter;
import net.streamline.api.configs.*;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;
import tv.quaint.savable.guilds.CreateGuildEvent;
import tv.quaint.savable.guilds.GuildChatEvent;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.CreatePartyEvent;
import tv.quaint.savable.parties.PartyChatEvent;
import tv.quaint.savable.parties.SavableParty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public class GroupManager {
    @Getter @Setter
    private static TreeMap<String, Class<? extends SavableGroup>> registeredClasses = new TreeMap<>();
    @Getter @Setter
    private static ConcurrentHashMap<Class<? extends SavableGroup>, Consumer<String>> registeredLoadOrders = new ConcurrentHashMap<>();

    public static void registerClass(Class<? extends SavableGroup> clazz, Consumer<String> consumer) {
        getRegisteredClasses().put(clazz.getSimpleName(), clazz);
        getRegisteredLoadOrders().put(clazz, consumer);
    }

    public static Class<? extends SavableGroup> getRegisteredClass(String name) {
        return getRegisteredClasses().get(name);
    }

    @Getter @Setter
    private static ConcurrentSkipListSet<GroupedUser> loadedGroupedUsers = new ConcurrentSkipListSet<>();

    public static void loadGroupedUser(GroupedUser user) {
        loadedGroupedUsers.add(user);
    }

    public static void unloadGroupedUser(GroupedUser user) {
        loadedGroupedUsers.remove(user);
    }

    private static GroupedUser getGroupedUser(String uuid) {
        for (GroupedUser user : loadedGroupedUsers) {
            if (user.getUuid().equals(uuid)) return user;
        }
        return null;
    }

    public static GroupedUser getOrGetGroupedUser(String uuid) {
        return getOrGetGroupedUser(uuid, true);
    }

    public static GroupedUser getOrGetGroupedUser(String uuid, boolean load) {
        GroupedUser user = getGroupedUser(uuid);
        if (user != null) return user;

        return new GroupedUser(uuid, load);
    }

    @Getter @Setter
    private static ConcurrentHashMap<Class<? extends SavableGroup>, ConcurrentSkipListSet<SavableGroup>> loadedGroups = new ConcurrentHashMap<>();

    public static void loadGroup(SavableGroup group) {
        ConcurrentSkipListSet<SavableGroup> groups = getGroupsOf(group.getClass());
        for (SavableGroup g : groups) {
            if (g.getUuid().equals(group.getUuid())) return;
        }
        groups.add(group);
        getLoadedGroups().put(group.getClass(), groups);
    }

    public static ConcurrentSkipListSet<SavableGroup> getGroupsOf(Class<? extends SavableGroup> clazz) {
        ConcurrentSkipListSet<SavableGroup> groups = getLoadedGroups().get(clazz);
        if (groups == null) groups = new ConcurrentSkipListSet<>();
        getLoadedGroups().put(clazz, groups);
        return groups;
    }

    public static void addGroupOf(Class<? extends SavableGroup> clazz, SavableGroup group) {
        getGroupsOf(clazz).add(group);
    }

    public static void removeGroupOf(SavableGroup group) {
        group.saveAll();
        getGroupsOf(group.getClass()).remove(group);
    }

    public static <T extends SavableGroup> T getGroupOfUser(Class<T> clazz, StreamlineUser user) {
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
            if (group.getUuid().equals(uuid)) return (T) group;
        }
        return null;
    }

    public static void removeGroupOf(Class<? extends SavableGroup> clazz, String uuid) {
        SavableGroup group = getGroup(clazz, uuid);
        if (group == null) return;
        removeGroupOf(group);
    }

    public static boolean hasGroup(StreamlineUser user, Class<? extends SavableGroup> clazz) {
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

    public static StorageResource<?> newStorageResource(String uuid, Class<? extends SavableGroup> clazz) {
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
                return new MongoResource(StreamlineGroups.getConfigs().getConfiguredDatabaseUsers(), clazz.getSimpleName(), "uuid", uuid);
            }
            case MYSQL -> {
                return new MySQLResource(StreamlineGroups.getConfigs().getConfiguredDatabaseUsers(), new SQLCollection(clazz.getSimpleName(), "uuid", uuid));
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

    public static SavableGuild createGuild(StreamlineUser sender, StreamlineUser leader, String name) {
        if (hasGroup(leader, SavableGuild.class)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyExists());
            return getGroupOfUser(SavableGuild.class, leader);
        }

        SavableGuild guild = new SavableGuild(leader.getUuid());
        guild.name = name;
        guild.saveAll();

        ModuleUtils.sendMessage(leader, StreamlineGroups.getMessages().guildsCreate());
        if (sender != leader) ModuleUtils.sendMessage(sender, leader, StreamlineGroups.getMessages().guildsCreate());

        ModuleUtils.fireEvent(new CreateGuildEvent(guild, leader));

        return guild;
    }

    public static SavableParty createParty(StreamlineUser sender, StreamlineUser leader) {
        if (hasGroup(leader, SavableParty.class)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyExists());
            return getGroupOfUser(SavableParty.class, leader);
        }

        SavableParty party = new SavableParty(leader.getUuid());
        party.saveAll();

        ModuleUtils.sendMessage(leader, StreamlineGroups.getMessages().partiesCreate());
        if (sender != leader) ModuleUtils.sendMessage(sender, leader, StreamlineGroups.getMessages().partiesCreate());

        ModuleUtils.fireEvent(new CreatePartyEvent(party, leader));

        return party;
    }

    public static void invitePlayerGuild(StreamlineUser sender, StreamlineUser other, StreamlineUser toInvite) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser user = getOrGetGroupedUser(toInvite.getUuid());
        if (user.getGroup(SavableGuild.class) != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInOther());
            return;
        }

        guild.addInvite(sender, toInvite);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().guildsSendInviteSender()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(toInvite, StreamlineGroups.getMessages().guildsSendInviteOther()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsSendInviteMembers()
                    .replace("%this_other%", toInvite.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        });
    }

    public static void invitePlayerParty(StreamlineUser sender, StreamlineUser other, StreamlineUser toInvite) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser user = getOrGetGroupedUser(toInvite.getUuid());
        if (user.getGroup(SavableParty.class) != null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseAlreadyInOther());
            return;
        }

        party.addInvite(sender, toInvite);

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().partiesSendInviteSender()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", party.owner.getName())
        );
        ModuleUtils.sendMessage(toInvite, StreamlineGroups.getMessages().partiesSendInviteOther()
                .replace("%this_other%", toInvite.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesSendInviteMembers()
                    .replace("%this_other%", toInvite.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        });
    }

    public static void acceptInviteGuild(StreamlineUser sender, StreamlineUser other, StreamlineUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser groupedInvited = getOrGetGroupedUser(invited.getUuid());
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
                .replace("%this_owner%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(other, StreamlineGroups.getMessages().guildsAcceptOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsAcceptMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        });
    }

    public static void acceptInviteParty(StreamlineUser sender, StreamlineUser other, StreamlineUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        GroupedUser groupedInvited = getOrGetGroupedUser(invited.getUuid());
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
                .replace("%this_owner%", party.owner.getName())
        );
        ModuleUtils.sendMessage(other, StreamlineGroups.getMessages().partiesAcceptOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesAcceptMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        });
    }

    public static void denyInviteGuild(StreamlineUser sender, StreamlineUser other, StreamlineUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
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
                .replace("%this_owner%", guild.owner.getName())
        );
        ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().guildsDenyOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", guild.owner.getName())
        );
        guild.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsDenyMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        });
    }

    public static void denyInviteParty(StreamlineUser sender, StreamlineUser other, StreamlineUser invited) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
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
                .replace("%this_owner%", party.owner.getName())
        );
        ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().partiesDenyOther()
                .replace("%this_other%", invited.getName())
                .replace("%this_sender%", sender.getName())
                .replace("%this_owner%", party.owner.getName())
        );
        party.getAllUsers().forEach(a -> {
            if (a.equals(sender)) return;
            ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesDenyMembers()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        });
    }

    public static void listGuild(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        StringBuilder forRoles = new StringBuilder();

        guild.groupRoleMap.getRolesOrdered().descendingMap().values().forEach(a -> {
            List<String> formattedNames = new ArrayList<>();
            guild.groupRoleMap.getUsersOf(a).forEach(act -> formattedNames.add(ModuleUtils.getFormatted(act)));
            forRoles.append(StreamlineGroups.getMessages().guildsListRole()
                    .replace("%this_role_identifier%", a.getIdentifier())
                    .replace("%this_role_name%", a.getName())
                    .replace("%this_role_max%", String.valueOf(a.getMax()))
                    .replace("%this_role_priority%", String.valueOf(a.getPriority()))
                    .replace("%this_role_flags%", ModuleUtils.getListAsFormattedString(a.getFlags().stream().toList()))
                    .replace("%this_role_members%", ModuleUtils.getListAsFormattedString(formattedNames))
            );
        });

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().guildsListMain()
                .replace("%this_for_roles%", forRoles)
        );
    }

    public static void listParty(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        StringBuilder forRoles = new StringBuilder();

        party.groupRoleMap.getRolesOrdered().descendingMap().values().forEach(a -> {
            List<String> formattedNames = new ArrayList<>();
            party.groupRoleMap.getUsersOf(a).forEach(act -> formattedNames.add(ModuleUtils.getFormatted(act)));
            forRoles.append(StreamlineGroups.getMessages().partiesListRole()
                    .replace("%this_role_identifier%", a.getIdentifier())
                    .replace("%this_role_name%", a.getName())
                    .replace("%this_role_max%", String.valueOf(a.getMax()))
                    .replace("%this_role_priority%", String.valueOf(a.getPriority()))
                    .replace("%this_role_flags%", ModuleUtils.getListAsFormattedString(a.getFlags().stream().toList()))
                    .replace("%this_role_members%", ModuleUtils.getListAsFormattedString(formattedNames))
            );
        });

        ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().partiesListMain()
                .replace("%this_for_roles%", forRoles)
        );
    }

    public static void disbandGuild(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (StreamlineUser user : guild.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(guild.owner)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandLeader()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDisbandMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        }

        guild.disband();
    }

    public static void disbandParty(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (StreamlineUser user : party.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(party.owner)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandLeader()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDisbandMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        }

        party.disband();
    }

    public static void promoteGuild(StreamlineUser sender, StreamlineUser other, StreamlineUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (promote.equals(sender)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteSelf());
            return;
        }

        if (guild.getRole(promote).hasFlag(GroupFlag.LEADER)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteLeader());
            return;
        }

        if (guild.hasMember(sender)) {
            if (! guild.userHasFlag(sender, GroupFlag.PROMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.PROMOTE));
                return;
            }
            if (guild.getRole(promote).equals(guild.getRole(sender))) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteSame());
                return;
            }
        }

        for (StreamlineUser user : guild.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        }

        guild.promoteUser(promote);
    }

    public static void promoteParty(StreamlineUser sender, StreamlineUser other, StreamlineUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (promote.equals(sender)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteSelf());
            return;
        }

        if (party.getRole(promote).hasFlag(GroupFlag.LEADER)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteLeader());
            return;
        }

        if (party.hasMember(sender)) {
            if (! party.userHasFlag(sender, GroupFlag.PROMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.PROMOTE));
                return;
            }
            if (party.getRole(promote).equals(party.getRole(sender))) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotPromoteSame());
                return;
            }
        }

        for (StreamlineUser user : party.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsPromoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        }

        party.promoteUser(promote);
    }

    public static void demoteGuild(StreamlineUser sender, StreamlineUser other, StreamlineUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (promote.equals(sender)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteSelf());
            return;
        }

        if (guild.getRole(promote).hasFlag(GroupFlag.LEADER)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteLeader());
            return;
        }

        if (guild.hasMember(sender)) {
            if (! guild.userHasFlag(sender, GroupFlag.DEMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.DEMOTE));
                return;
            }
            if (guild.getRole(promote).equals(guild.getRole(sender))) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteSame());
                return;
            }
        }

        for (StreamlineUser user : guild.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsDemoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        }

        guild.demoteUser(promote);
    }

    public static void demoteParty(StreamlineUser sender, StreamlineUser other, StreamlineUser promote) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasMember(promote)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        if (promote.equals(sender)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteSelf());
            return;
        }

        if (party.getRole(promote).hasFlag(GroupFlag.LEADER)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteLeader());
            return;
        }

        if (party.hasMember(sender)) {
            if (! party.userHasFlag(sender, GroupFlag.DEMOTE)) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorWithoutFlag(GroupFlag.DEMOTE));
                return;
            }
            if (party.getRole(promote).equals(party.getRole(sender))) {
                ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseCannotDemoteSame());
                return;
            }
        }

        for (StreamlineUser user : party.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDemoteSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(promote)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDemoteOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesDemoteMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        }

        party.demoteUser(promote);
    }

    public static void leaveGuild(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! guild.hasMember(other)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        for (StreamlineUser user : guild.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsLeaveSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            if (user.equals(other)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsLeaveOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsLeaveMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", guild.owner.getName())
            );
        }

        guild.removeMember(other);
    }

    public static void leaveParty(StreamlineUser sender, StreamlineUser other) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        if (! party.hasMember(other)) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotInOther());
            return;
        }

        for (StreamlineUser user : party.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesLeaveSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            if (user.equals(other)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesLeaveOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", party.owner.getName())
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesLeaveMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_owner%", party.owner.getName())
            );
        }

        party.removeMember(other);
    }

    public static void chatGuild(StreamlineUser sender, StreamlineUser other, String message) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (StreamlineUser user : guild.getAllUsers()) {
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsChat()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_message%", message)
            );
        }

        ModuleUtils.fireEvent(new GuildChatEvent(guild, sender, message));
    }

    public static void chatParty(StreamlineUser sender, StreamlineUser other, String message) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableParty party = groupedUser.getGroup(SavableParty.class);

        if (party == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        for (StreamlineUser user : party.getAllUsers()) {
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().partiesChat()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_message%", message)
            );
        }

        ModuleUtils.fireEvent(new PartyChatEvent(party, sender, message));
    }

    public static void renameGuild(StreamlineUser sender, StreamlineUser other, String name) {
        GroupedUser groupedUser = getOrGetGroupedUser(other.getUuid());
        SavableGuild guild = groupedUser.getGroup(SavableGuild.class);

        if (guild == null) {
            ModuleUtils.sendMessage(sender, StreamlineGroups.getMessages().errorsBaseNotExists());
            return;
        }

        String oldName = guild.name;

        for (StreamlineUser user : guild.getAllUsers()) {
            if (user.equals(sender)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsRenameSender()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                        .replace("%this_name_new%", name)
                        .replace("%this_name_old%", oldName)
                );
                continue;
            }
            if (user.equals(guild.owner)) {
                ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsRenameOther()
                        .replace("%this_sender%", sender.getName())
                        .replace("%this_other%", other.getName())
                        .replace("%this_owner%", guild.owner.getName())
                        .replace("%this_name_new%", name)
                        .replace("%this_name_old%", oldName)
                );
                continue;
            }
            ModuleUtils.sendMessage(user, StreamlineGroups.getMessages().guildsRenameMembers()
                    .replace("%this_sender%", sender.getName())
                    .replace("%this_other%", other.getName())
                    .replace("%this_leader%", guild.owner.getName())
                    .replace("%this_name_new%", name)
                    .replace("%this_name_old%", oldName)
            );
        }

        guild.setNameReturnOld(name);
    }
}
