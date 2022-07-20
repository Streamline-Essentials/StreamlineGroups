package tv.quaint.savable;


import net.luckperms.api.model.user.User;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.SavableConsole;
import net.streamline.api.savables.users.SavableUser;
import tv.quaint.StreamlineGroups;

import java.util.*;

public abstract class SavableGroup extends SavableResource {
    public SavableUser leader;
    public List<SavableUser> moderators = new ArrayList<>();
    public List<SavableUser> members = new ArrayList<>();
    public List<SavableUser> totalMembers = new ArrayList<>();
    public List<InviteTicker<? extends SavableGroup>> invites = new ArrayList<>();
    public boolean isMuted;
    public boolean isPublic;
    public int maxSize;
    public Date createDate;

    public enum Level {
        MEMBER,
        MODERATOR,
        LEADER
    }

    public SavableGroup(SavableUser leader, Class<? extends SavableResource> clazz) {
        this(leader.uuid, clazz);
    }

    public SavableGroup(String uuid, Class<? extends SavableResource> clazz) {
        super(uuid, GroupManager.newStorageResource(uuid, clazz));
        this.leader = ModuleUtils.getOrGetUser(uuid);
        GroupManager.loadGroup(this);
        addToTMembers(this.leader);
    }

    public void populateDefaults() {
        // Users.
        moderators = parseUserListFromUUIDs(getOrSetDefault("users.moderators", new ArrayList<>()));
        members = parseUserListFromUUIDs(getOrSetDefault("users.members", new ArrayList<>()));
        totalMembers = parseUserListFromUUIDs(getOrSetDefault("users.total", List.of(uuid)));
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", false);
        isPublic = getOrSetDefault("settings.public.toggled", false);
        maxSize = getOrSetDefault("settings.size.max", StreamlineGroups.getConfigs().baseMax("default"));
        createDate = new Date(getOrSetDefault("create-date", new Date().getTime()));

        populateMoreDefaults();
    }

    public List<SavableUser> parseUserListFromUUIDs(List<String> uuids) {
        List<SavableUser> users = new ArrayList<>();

        for (String uuid : uuids) {
            SavableUser u = ModuleUtils.getOrGetUser(uuid);

            if (users.contains(u)) continue;

            users.add(u);
        }

        return users;
    }

    public List<String> parseUUIDListFromUsers(List<SavableUser> users) {
        List<String> uuids = new ArrayList<>();

        for (SavableUser user : users) {
            if (uuids.contains(user.uuid)) continue;

            uuids.add(user.uuid);
        }

        return uuids;
    }

    abstract public void populateMoreDefaults();

    public void loadValues(){
        // Users.
        moderators = parseUserListFromUUIDs(getOrSetDefault("users.moderators", new ArrayList<>()));
        members = parseUserListFromUUIDs(getOrSetDefault("users.members", new ArrayList<>()));
        totalMembers = parseUserListFromUUIDs(getOrSetDefault("users.total", List.of(uuid)));
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", isMuted);
        isPublic = getOrSetDefault("settings.public.toggled", isPublic);
        maxSize = getOrSetDefault("settings.size.max", maxSize);
        createDate = new Date(getOrSetDefault("create-date", createDate.getTime()));

        loadMoreValues();
    }

    abstract public void loadMoreValues();

    public void saveAll() {
        // Users.
        set("users.moderators", parseUUIDListFromUsers(moderators));
        set("users.members", parseUUIDListFromUsers(members));
        set("users.total", parseUUIDListFromUsers(totalMembers));
        // Settings.
        set("settings.mute.toggled", isMuted);
        set("settings.public.toggled", isPublic);
        set("create-date", createDate.getTime());

        saveMore();
    }

    abstract public void saveMore();

    public SavableUser getMember(String uuid) {
        return ModuleUtils.getOrGetUser(uuid);
    }

    public void removeUUIDCompletely(String uuid) {
        SavableUser user = ModuleUtils.getOrGetUser(uuid);
        if (user == null) return;

        moderators.remove(user);
        members.remove(user);
        totalMembers.remove(user);
    }

    public boolean hasInvite(SavableUser user) {
        for (SavableUser u : getInvitesAsUsers()) {
            if (u.uuid.equals(user.uuid)) return true;
        }
        return false;
    }

    public boolean hasMember(String uuid){
        for (SavableUser user : totalMembers) {
            if (user.uuid.equals(uuid)) return true;
        }

        return false;
    }

    public boolean hasMember(SavableUser stat){
        return hasMember(stat.uuid);
    }

    public int getSize(){
        return totalMembers.size();
    }

    public void removeFromModerators(SavableUser stat){
        if (! moderators.contains(stat)) return;
        moderators.remove(stat);
    }

    public void remFromMembers(SavableUser stat){
        if (! members.contains(stat)) return;
        members.remove(stat);
    }

    public void remFromTMembers(SavableUser stat){
        if (! totalMembers.contains(stat)) return;
        totalMembers.remove(stat);
        GroupedUser user = GroupManager.getOrGetGroupedUser(stat.uuid);
        user.disassociateWith(getClass());
    }

    public InviteTicker<? extends SavableGroup> getInviteTicker(SavableUser invited) {
        for (InviteTicker<? extends SavableGroup> inviteTicker : invites) {
            if (inviteTicker.getInvited().equals(invited)) return inviteTicker;
        }

        return null;
    }

    public void remFromInvites(InviteTicker<? extends SavableGroup> ticker){
        if (! getInvitesAsUsers().contains(ticker.getInvited())) return;
        invites.remove(ticker);
    }

    public void remFromInvites(SavableUser user){
        if (! getInvitesAsUsers().contains(user)) return;
        invites.remove(getInviteTicker(user));
    }

    public void remFromInvitesCompletely(SavableUser user){
        if (! getInvitesAsUsers().contains(user)) return;
        invites.remove(getInviteTicker(user));
        totalMembers.remove(user);
    }

    public void addToModerators(SavableUser stat){
        if (moderators.contains(stat)) return;
        moderators.add(stat);
    }

    public void addToMembers(SavableUser stat){
        if (members.contains(stat)) return;
        members.add(stat);
    }

    public void addToTMembers(SavableUser stat){
        if (totalMembers.contains(stat)) return;
        totalMembers.add(stat);
        GroupedUser user = GroupManager.getOrGetGroupedUser(stat.uuid);
        user.associateWith(this.getClass(), this.uuid);
    }

    public List<SavableUser> getInvitesAsUsers() {
        List<SavableUser> users = new ArrayList<>();

        invites.forEach(a -> users.add(a.getInvited()));

        return users;
    }

    public void addInvite(SavableUser inviter, SavableUser to) {
        if (getInvitesAsUsers().contains(to)) return;
        invites.add(new InviteTicker<>(this, to, inviter));
        ModuleUtils.fireEvent(new InviteCreateEvent<>(this, to, inviter));
    }

    public void addMember(SavableUser stat){
        addToTMembers(stat);
        addToMembers(stat);
    }

    public void removeMemberFromGroup(SavableUser stat){
        Random RNG = new Random();

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    totalMembers.remove(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        totalMembers.remove(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }

        remFromInvitesCompletely(stat);
        removeFromModerators(stat);
        remFromMembers(stat);
        remFromTMembers(stat);
    }

    public void setMuted(boolean bool) {
        isMuted = bool;
    }

    public void toggleMute(){
        setMuted(! isMuted);
    }

    public void setPublic(boolean bool){
        isPublic = bool;
    }

    public void togglePublic() {
        setPublic(! isPublic);
    }

    public Level getLevel(SavableUser member){
        if (this.members.contains(member))
            return Level.MEMBER;
        else if (this.moderators.contains(member))
            return Level.MODERATOR;
        else if (this.uuid.equals(member.uuid))
            return Level.LEADER;
        else
            return Level.MEMBER;
    }

    public void setModerator(SavableUser stat){
        Random RNG = new Random();

        remFromMembers(stat);

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    moderators.add(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        moderators.add(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }

        addToModerators(stat);
    }

    public void setMember(SavableUser stat){
        Random RNG = new Random();

        removeFromModerators(stat);

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    members.add(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        members.add(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }

        addToMembers(stat);
        addToTMembers(stat);
    }

    public void replaceLeader(SavableUser with) {
        try {
            storageResource.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addToModerators(ModuleUtils.getOrGetUser(uuid));
        removeFromModerators(with);
        remFromMembers(with);
        remFromInvitesCompletely(with);

        this.uuid = with.uuid;

        try {
            saveAll();
            loadValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasModPerms(String uuid) {
        try {
            return hasModPerms(ModuleUtils.getOrGetUser(uuid));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasModPerms(SavableUser stat) {
        try {
            return moderators.contains(stat) || uuid.equals(stat.uuid);
        } catch (Exception e) {
            return false;
        }
    }

    public void setMaxSize(int size){
        SavableUser user = ModuleUtils.getOrGetUser(uuid);
        if (user == null) return;

        if (size <= getMaxSize(user))
            this.maxSize = size;
    }

    public int getMaxSize(SavableUser leader){
        if (leader instanceof SavableConsole) {
            return StreamlineGroups.getConfigs().baseMax("default");
        }

        try {
            User user = ModuleUtils.getLuckPerms().getUserManager().getUser(leader.latestName);
            if (user == null) {
                StreamlineGroups.getInstance().logInfo("Could not get LuckPerms user with name '" + leader.latestName + "'.");
                return StreamlineGroups.getConfigs().baseMax("default");
            }
            String group = user.getPrimaryGroup();

            return StreamlineGroups.getConfigs().baseMax(group);
        } catch (Exception e) {
            e.printStackTrace();
            return StreamlineGroups.getConfigs().baseMax("default");
        }
    }

    public void disband(){
        storageResource.delete();

        GroupManager.removeGroupOf(this);

        for (SavableUser member : totalMembers) {
            GroupedUser user = GroupManager.getOrGetGroupedUser(member.uuid);
            user.disassociateWith(this.getClass());
        }

        try {
            dispose();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

