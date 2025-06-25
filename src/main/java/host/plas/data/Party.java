package host.plas.data;


import gg.drak.thebase.objects.Identified;
import host.plas.data.events.InviteCreateEvent;
import host.plas.data.invites.InviteTicker;
import host.plas.data.roles.GroupRoleMap;
import host.plas.data.roles.SavableGroupRole;
import lombok.Getter;
import lombok.Setter;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.streamline.api.permissions.LuckPermsHandler;
import singularity.data.players.CosmicPlayer;
import singularity.modules.ModuleUtils;
import singularity.data.console.CosmicSender;
import host.plas.StreamlineGroups;
import host.plas.data.flags.GroupFlag;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter @Setter
public class Party implements Identified {
    private String uuid;

    public String getIdentifier() {
        return getUuid();
    }

    public void setIdentifier(String identifier) {
        setUuid(identifier);
    }

    private CosmicSender owner;
    private ConcurrentSkipListSet<InviteTicker> invites = new ConcurrentSkipListSet<>();
    private boolean isMuted;
    private boolean isPublic;
    private int maxSize;
    private Date createDate;
    private GroupRoleMap groupRoleMap;

    public Party(String uuid, CosmicSender owner, boolean load) {
        this.uuid = uuid;

        populateDefaults();

        this.owner = owner;
        this.maxSize = getMaxSize(owner);
        this.groupRoleMap.applyUser(groupRoleMap.getRolesOrdered().lastEntry().getValue(), this.owner);

        if (! isLoaded() && load) load();
    }

    public Party(String uuid, CosmicSender owner) {
        this(uuid, owner, true);
    }

    public Party(CosmicSender owner, boolean load) {
        this(UUID.randomUUID().toString(), owner, load);
    }

    public Party(CosmicSender owner) {
        this(owner, true);
    }

    public void load() {
        GroupManager.load(this);
    }

    public void unload() {
        GroupManager.unload(this);
    }

    public boolean isLoaded() {
        return GroupManager.isLoaded(this);
    }

    public void populateDefaults() {
        // Settings.
        isMuted = false;
        isPublic = false;
        maxSize = StreamlineGroups.getConfigs().baseMax("default");
        createDate = new Date();
        groupRoleMap = new GroupRoleMap(this);
        getGroupRoleMap().clearRoles();
        getGroupRoleMap().addAllRoles(GroupRoleMap.getDefaultRoles());
    }

    public ConcurrentSkipListSet<CosmicSender> parseUserListFromUUIDs(ConcurrentSkipListSet<String> uuids) {
        ConcurrentSkipListSet<CosmicSender> users = new ConcurrentSkipListSet<>();

        for (String uuid : uuids) {
            CosmicSender u = ModuleUtils.getOrCreateSender(uuid).orElse(null);
            if (u == null) continue;

            if (users.contains(u)) continue;

            users.add(u);
        }

        return users;
    }

    public ConcurrentSkipListSet<String> parseUUIDListFromUsers(ConcurrentSkipListSet<CosmicSender> users) {
        ConcurrentSkipListSet<String> uuids = new ConcurrentSkipListSet<>();

        for (CosmicSender user : users) {
            if (uuids.contains(user.getUuid())) continue;

            uuids.add(user.getUuid());
        }

        return uuids;
    }

    public void addMember(CosmicSender user) {
        groupRoleMap.addUser(user);
        remFromInvites(user);
    }

    public void removeMember(CosmicSender user) {
        groupRoleMap.removeUserAll(user);
        remFromInvites(user);
    }

    public CosmicSender getMember(String uuid) {
        return ModuleUtils.getOrCreateSender(uuid).orElse(null);
    }

    public ConcurrentSkipListSet<CosmicSender> getAllUsers() {
        return groupRoleMap.getAllUsers();
    }

    public boolean hasInvite(CosmicSender user) {
        for (CosmicSender u : getInvitesAsUsers()) {
            if (u.getUuid().equals(user.getUuid())) return true;
        }
        return false;
    }

    public boolean hasMember(CosmicSender stat){
        return groupRoleMap.hasUser(stat);
    }

    public int getSize(){
        return groupRoleMap.size();
    }

    public InviteTicker getInviteTicker(CosmicSender invited) {
        for (InviteTicker inviteTicker : invites) {
            if (inviteTicker.getInvited().equals(invited)) return inviteTicker;
        }

        return null;
    }

    public void remFromInvites(InviteTicker ticker){
        if (! getInvitesAsUsers().contains(ticker.getInvited())) return;
        invites.remove(ticker);
    }

    public void remFromInvites(CosmicSender user){
        if (! getInvitesAsUsers().contains(user)) return;
        InviteTicker ticker = getInviteTicker(user);
        ticker.cancel();
        invites.remove(ticker);
    }

    public void remFromInvitesCompletely(CosmicSender user){
        if (! getInvitesAsUsers().contains(user)) return;
        invites.remove(getInviteTicker(user));
        groupRoleMap.removeUserAll(user);
    }

    public ConcurrentSkipListSet<CosmicSender> getInvitesAsUsers() {
        ConcurrentSkipListSet<CosmicSender> users = new ConcurrentSkipListSet<>();

        invites.forEach(a -> users.add(a.getInvited()));

        return users;
    }

    public void addInvite(CosmicSender inviter, CosmicSender to) {
        if (getInvitesAsUsers().contains(to)) return;
        invites.add(new InviteTicker(this, to, inviter));
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

    public SavableGroupRole getRole(CosmicSender member){
        return groupRoleMap.getRoleOf(member);
    }

    public boolean userHasFlag(CosmicSender user, GroupFlag flag) {
        return groupRoleMap.userHas(user, flag);
    }

    public void setMaxSize(int size){
        CosmicSender user = ModuleUtils.getOrCreateSender(getUuid()).orElse(null);
        if (user == null) return;

        if (size <= getMaxSize(user))
            this.maxSize = size;
    }

    public int getMaxSize(CosmicSender leader){
        if (! (leader instanceof CosmicPlayer)) {
            return StreamlineGroups.getConfigs().baseMax("default");
        }

        try {
            if (LuckPermsHandler.hasLuckPerms()) {
                User user = LuckPermsProvider.get().getUserManager().getUser(leader.getCurrentName());
                if (user == null) {
                    StreamlineGroups.getInstance().logInfo("Could not get LuckPerms user with name '" + leader.getCurrentName() + "'.");
                    return StreamlineGroups.getConfigs().baseMax("default");
                }
                String group = user.getPrimaryGroup();

                return StreamlineGroups.getConfigs().baseMax(group);
            } else {
                return StreamlineGroups.getConfigs().baseMax("default");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return StreamlineGroups.getConfigs().baseMax("default");
        }
    }

    public void setMemberLevel(CosmicSender user, SavableGroupRole role) {
        groupRoleMap.applyUser(role, user);
    }

    public void promoteUser(CosmicSender user) {
        groupRoleMap.promote(user);
    }

    public void demoteUser(CosmicSender user) {
        groupRoleMap.demote(user);
    }

    public void disband() {
        if (isLoaded()) {
            try {
                unload();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        try {
            dispose();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void dispose() throws Throwable {
        try {
            if (getAllUsers().isEmpty()) {
                unload();
                return;
            }
            for (CosmicSender user : getAllUsers()) {
                remFromInvitesCompletely(user);
            }
            groupRoleMap.clearRoles();
            groupRoleMap = null;
            owner = null;
            invites.clear();
            invites = null;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        finalize();
    }
}

