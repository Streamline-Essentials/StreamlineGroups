package tv.quaint.savable;


import net.luckperms.api.model.user.User;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.SavableConsole;
import net.streamline.api.savables.users.SavableUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

import java.util.*;

public abstract class SavableGroup extends SavableResource {
    public SavableUser owner;
    public List<InviteTicker<? extends SavableGroup>> invites = new ArrayList<>();
    public boolean isMuted;
    public boolean isPublic;
    public int maxSize;
    public Date createDate;
    public GroupRoleMap groupRoleMap;

    public SavableGroup(SavableUser owner, Class<? extends SavableResource> clazz) {
        this(owner.uuid, clazz);
    }

    public SavableGroup(String uuid, Class<? extends SavableResource> clazz) {
        super(uuid, GroupManager.newStorageResource(uuid, clazz));
        this.owner = ModuleUtils.getOrGetUser(uuid);
        groupRoleMap = new GroupRoleMap(this);
        groupRoleMap.applyUser(groupRoleMap.getRolesOrdered().lastEntry().getValue(), this.owner);
        GroupedUser u = GroupManager.getOrGetGroupedUser(uuid, false);
        u.associateWith(this.getClass(), this.uuid);
        GroupManager.loadGroup(this);
    }

    public void populateDefaults() {
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
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", isMuted);
        isPublic = getOrSetDefault("settings.public.toggled", isPublic);
        maxSize = getOrSetDefault("settings.size.max", maxSize);
        createDate = new Date(getOrSetDefault("create-date", createDate.getTime()));

        loadMoreValues();
    }

    abstract public void loadMoreValues();

    public void saveAll() {
        // Roles.
        groupRoleMap.save();
        // Settings.
        set("settings.mute.toggled", isMuted);
        set("settings.public.toggled", isPublic);
        set("create-date", createDate.getTime());

        saveMore();
    }

    abstract public void saveMore();

    public void addMember(SavableUser user) {
        groupRoleMap.addUser(user);
        remFromInvites(user);
        GroupedUser u = GroupManager.getOrGetGroupedUser(user.uuid);
        u.associateWith(this.getClass(), this.uuid);
    }

    public void removeMember(SavableUser user) {
        groupRoleMap.removeUserAll(user);
        remFromInvites(user);
        GroupedUser u = GroupManager.getOrGetGroupedUser(user.uuid);
        u.disassociateWith(this.getClass(), this.uuid);
    }

    public void setOwner(SavableUser user) {
        this.owner = user;
        this.storageResource.delete();
        this.storageResource = GroupManager.newStorageResource(user.uuid, this.getClass());
        if (this.storageResource == null) {
            StreamlineGroups.getInstance().logSevere(this.getClass().getSimpleName() + " with uuid '" + this.uuid + "' could not set the owner!");
            return;
        }
        this.storageResource.reloadResource(true);
        this.saveAll();
    }

    public SavableUser getMember(String uuid) {
        return ModuleUtils.getOrGetUser(uuid);
    }

    public List<SavableUser> getAllUsers() {
        return groupRoleMap.getAllUsers();
    }

    public boolean hasInvite(SavableUser user) {
        for (SavableUser u : getInvitesAsUsers()) {
            if (u.uuid.equals(user.uuid)) return true;
        }
        return false;
    }

    public boolean hasMember(SavableUser stat){
        return groupRoleMap.hasUser(stat);
    }

    public int getSize(){
        return groupRoleMap.size();
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
        InviteTicker<?> ticker = getInviteTicker(user);
        ticker.cancel();
        invites.remove(ticker);
    }

    public void remFromInvitesCompletely(SavableUser user){
        if (! getInvitesAsUsers().contains(user)) return;
        invites.remove(getInviteTicker(user));
        groupRoleMap.removeUserAll(user);
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

    public SavableGroupRole getRole(SavableUser member){
        return groupRoleMap.getRoleOf(member);
    }

    public boolean userHasFlag(SavableUser user, GroupFlag flag) {
        return groupRoleMap.userHas(user, flag);
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

    public void setMemberLevel(SavableUser user, SavableGroupRole role) {
        groupRoleMap.applyUser(role, user);
    }

    public void promoteUser(SavableUser user) {
        groupRoleMap.promote(user);
    }

    public void demoteUser(SavableUser user) {
        groupRoleMap.demote(user);
    }

    public void disband(){
        for (SavableUser member : groupRoleMap.getAllUsers()) {
            GroupedUser user = GroupManager.getOrGetGroupedUser(member.uuid);
            user.disassociateWith(this.getClass(), this.uuid);
        }

        GroupManager.removeGroupOf(this);

        storageResource.delete();

        try {
            dispose();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

