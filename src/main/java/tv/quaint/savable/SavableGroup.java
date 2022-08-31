package tv.quaint.savable;


import net.luckperms.api.model.user.User;
import net.streamline.api.configs.StorageResource;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.StreamlineConsole;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

import java.util.*;

public abstract class SavableGroup extends SavableResource {
    @Override
    public StorageResource<?> getStorageResource() {
        return storageResource;
    }

    public StreamlineUser owner;
    public List<InviteTicker<? extends SavableGroup>> invites = new ArrayList<>();
    public boolean isMuted;
    public boolean isPublic;
    public int maxSize;
    public Date createDate;
    public GroupRoleMap groupRoleMap;

    public SavableGroup(StreamlineUser owner, Class<? extends SavableGroup> clazz) {
        this(owner.getUUID(), clazz);
    }

    public SavableGroup(String uuid, Class<? extends SavableGroup> clazz) {
        super(uuid, GroupManager.newStorageResource(uuid, clazz));
        this.owner = ModuleUtils.getOrGetUser(uuid);
        groupRoleMap = new GroupRoleMap(this);
        groupRoleMap.applyUser(groupRoleMap.getRolesOrdered().lastEntry().getValue(), this.owner);
        GroupedUser u = GroupManager.getOrGetGroupedUser(uuid, false);
        u.associateWith(this.getClass(), this.uuid);
        GroupManager.loadGroup(this);

        StreamlineGroups.getInstance().logInfo("Done creating object!");
        StreamlineGroups.getInstance().logInfo("Exists: " + storageResource.exists());
    }

    @Override
    public void populateDefaults() {
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", false);
        isPublic = getOrSetDefault("settings.public.toggled", false);
        maxSize = getOrSetDefault("settings.size.max", StreamlineGroups.getConfigs().baseMax("default"));
        createDate = new Date(getOrSetDefault("create-date", new Date().getTime()));

        populateMoreDefaults();
    }

    public List<StreamlineUser> parseUserListFromUUIDs(List<String> uuids) {
        List<StreamlineUser> users = new ArrayList<>();

        for (String uuid : uuids) {
            StreamlineUser u = ModuleUtils.getOrGetUser(uuid);

            if (users.contains(u)) continue;

            users.add(u);
        }

        return users;
    }

    public List<String> parseUUIDListFromUsers(List<StreamlineUser> users) {
        List<String> uuids = new ArrayList<>();

        for (StreamlineUser user : users) {
            if (uuids.contains(user.getUUID())) continue;

            uuids.add(user.getUUID());
        }

        return uuids;
    }

    abstract public void populateMoreDefaults();

    @Override
    public void loadValues(){
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", isMuted);
        isPublic = getOrSetDefault("settings.public.toggled", isPublic);
        maxSize = getOrSetDefault("settings.size.max", maxSize);
        createDate = new Date(getOrSetDefault("create-date", createDate.getTime()));

        loadMoreValues();
    }

    abstract public void loadMoreValues();

    @Override
    public void saveAll() {
        // Roles.
        groupRoleMap.save();

        // Settings.
        set("settings.mute.toggled", isMuted);
        set("settings.public.toggled", isPublic);
        set("create-date", createDate.getTime());

        saveMore();

        storageResource.sync();
    }

    abstract public void saveMore();

    public void addMember(StreamlineUser user) {
        groupRoleMap.addUser(user);
        remFromInvites(user);
        GroupedUser u = GroupManager.getOrGetGroupedUser(user.getUUID());
        u.associateWith(this.getClass(), this.uuid);
    }

    public void removeMember(StreamlineUser user) {
        groupRoleMap.removeUserAll(user);
        remFromInvites(user);
        GroupedUser u = GroupManager.getOrGetGroupedUser(user.getUUID());
        u.disassociateWith(this.getClass(), this.uuid);
    }

    public void setOwner(StreamlineUser user) {
        this.owner = user;
        this.storageResource.delete();
        this.storageResource = GroupManager.newStorageResource(user.getUUID(), this.getClass());
        if (this.storageResource == null) {
            StreamlineGroups.getInstance().logSevere(this.getClass().getSimpleName() + " with uuid '" + this.uuid + "' could not set the owner!");
            return;
        }
        this.storageResource.reloadResource(true);
        this.saveAll();
    }

    public StreamlineUser getMember(String uuid) {
        return ModuleUtils.getOrGetUser(uuid);
    }

    public List<StreamlineUser> getAllUsers() {
        return groupRoleMap.getAllUsers();
    }

    public boolean hasInvite(StreamlineUser user) {
        for (StreamlineUser u : getInvitesAsUsers()) {
            if (u.getUUID().equals(user.getUUID())) return true;
        }
        return false;
    }

    public boolean hasMember(StreamlineUser stat){
        return groupRoleMap.hasUser(stat);
    }

    public int getSize(){
        return groupRoleMap.size();
    }

    public InviteTicker<? extends SavableGroup> getInviteTicker(StreamlineUser invited) {
        for (InviteTicker<? extends SavableGroup> inviteTicker : invites) {
            if (inviteTicker.getInvited().equals(invited)) return inviteTicker;
        }

        return null;
    }

    public void remFromInvites(InviteTicker<? extends SavableGroup> ticker){
        if (! getInvitesAsUsers().contains(ticker.getInvited())) return;
        invites.remove(ticker);
    }

    public void remFromInvites(StreamlineUser user){
        if (! getInvitesAsUsers().contains(user)) return;
        InviteTicker<?> ticker = getInviteTicker(user);
        ticker.cancel();
        invites.remove(ticker);
    }

    public void remFromInvitesCompletely(StreamlineUser user){
        if (! getInvitesAsUsers().contains(user)) return;
        invites.remove(getInviteTicker(user));
        groupRoleMap.removeUserAll(user);
    }

    public List<StreamlineUser> getInvitesAsUsers() {
        List<StreamlineUser> users = new ArrayList<>();

        invites.forEach(a -> users.add(a.getInvited()));

        return users;
    }

    public void addInvite(StreamlineUser inviter, StreamlineUser to) {
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

    public SavableGroupRole getRole(StreamlineUser member){
        return groupRoleMap.getRoleOf(member);
    }

    public boolean userHasFlag(StreamlineUser user, GroupFlag flag) {
        return groupRoleMap.userHas(user, flag);
    }

    public void setMaxSize(int size){
        StreamlineUser user = ModuleUtils.getOrGetUser(uuid);
        if (user == null) return;

        if (size <= getMaxSize(user))
            this.maxSize = size;
    }

    public int getMaxSize(StreamlineUser leader){
        if (leader instanceof StreamlineConsole) {
            return StreamlineGroups.getConfigs().baseMax("default");
        }

        try {
            User user = ModuleUtils.getLuckPerms().getUserManager().getUser(leader.getLatestName());
            if (user == null) {
                StreamlineGroups.getInstance().logInfo("Could not get LuckPerms user with name '" + leader.getLatestName() + "'.");
                return StreamlineGroups.getConfigs().baseMax("default");
            }
            String group = user.getPrimaryGroup();

            return StreamlineGroups.getConfigs().baseMax(group);
        } catch (Exception e) {
            e.printStackTrace();
            return StreamlineGroups.getConfigs().baseMax("default");
        }
    }

    public void setMemberLevel(StreamlineUser user, SavableGroupRole role) {
        groupRoleMap.applyUser(role, user);
    }

    public void promoteUser(StreamlineUser user) {
        groupRoleMap.promote(user);
    }

    public void demoteUser(StreamlineUser user) {
        groupRoleMap.demote(user);
    }

    public void disband(){
        for (StreamlineUser member : groupRoleMap.getAllUsers()) {
            GroupedUser user = GroupManager.getOrGetGroupedUser(member.getUUID());
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

