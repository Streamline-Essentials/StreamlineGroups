package tv.quaint.savable;

import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class GroupRoleMap {
    public SavableGroup group;
    public ConcurrentSkipListMap<SavableGroupRole, ConcurrentSkipListSet<StreamlineUser>> roles = new ConcurrentSkipListMap<>();

    public GroupRoleMap(SavableGroup group) {
        this.group = group;
        get();
    }

    public List<String> getIdentifiers() {
        List<String> r = new ArrayList<>();

        for (String k : this.group.getStorageResource().getMap().keySet()) {
            if (! k.startsWith("roles.")) continue;
            try {
                String toAdd = k.replace("roles.", "").split("\\.", 2)[0];
                if (r.contains(toAdd)) continue;
                r.add(toAdd);
            } catch (Exception e) {
                // do nothing.
            }
        }

        return r;
    }

    public void get() {
        getIdentifiers().forEach(a -> {
            int priority = this.group.getStorageResource().getOrSetDefault("roles." + a + ".priority", 1);
            int max = this.group.getStorageResource().getOrSetDefault("roles." + a + ".max", -1);
            String name = this.group.getStorageResource().getOrSetDefault("roles." + a + ".name", "&cNAME");
            ConcurrentSkipListSet<String> flags = new ConcurrentSkipListSet<>(this.group.getStorageResource().getOrSetDefault("roles." + a + ".flags", new ArrayList<>()));
            ConcurrentSkipListSet<String> memberUUIDs = new ConcurrentSkipListSet<>(this.group.getStorageResource().getOrSetDefault("roles." + a + ".members", new ArrayList<>()));

            SavableGroupRole role = new SavableGroupRole(a, priority, name, max, flags);
            ConcurrentSkipListSet<StreamlineUser> users = new ConcurrentSkipListSet<>();
            memberUUIDs.forEach(act -> {
                users.add(ModuleUtils.getOrGetUser(act));
            });
            addSavableGroup(role, users);
        });

        if (roles.isEmpty()) {
            try {
                StreamlineGroups.getDefaultRoles().getDefaultRolesOf(this.group.getClass()).forEach(a -> {
                    addSavableGroup(new SavableGroupRole(a.identifier(), a.priority(), a.name(), a.max(), a.flags()));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (roles.isEmpty()) {
            try {
                StreamlineGroups.getDefaultRoles().getDefaultRolesOf(this.group.getClass()).forEach(a -> {
                    addSavableGroup(new SavableGroupRole(a.identifier(), a.priority(), a.name(), a.max(), a.flags()));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        getRoles().forEach(a -> {
            this.group.set("roles." + a.getIdentifier() + ".priority", a.getPriority());
            this.group.set("roles." + a.getIdentifier() + ".max", a.getMax());
            this.group.set("roles." + a.getIdentifier() + ".name", a.getName());
            this.group.set("roles." + a.getIdentifier() + ".flags", a.getFlags());

            List<String> memberUUIDs = new ArrayList<>();
            getUsersOf(a).forEach(act -> {
                memberUUIDs.add(act.getUuid());
            });
            this.group.set("roles." + a.getIdentifier() + ".members", memberUUIDs);
        });
    }

    public void addSavableGroup(SavableGroupRole role, ConcurrentSkipListSet<StreamlineUser> users) {
        roles.put(role, users);
    }

    public void addSavableGroup(SavableGroupRole role) {
        addSavableGroup(role, new ConcurrentSkipListSet<>());
    }

    public ConcurrentSkipListSet<StreamlineUser> getUsersOf(SavableGroupRole role) {
        ConcurrentSkipListSet<StreamlineUser> users = roles.get(role);
        if (users == null) return new ConcurrentSkipListSet<>();
        return users;
    }

    public void applyUser(SavableGroupRole role, StreamlineUser user) {
        removeUserAll(user);
        ConcurrentSkipListSet<StreamlineUser> users = getUsersOf(role);
        users.add(user);
    }

    public int size() {
        int r = 0;
        for (SavableGroupRole role : getRoles()) {
            r += getUsersOf(role).size();
        }
        return r;
    }

    public ConcurrentSkipListSet<StreamlineUser> getAllUsers() {
        ConcurrentSkipListSet<StreamlineUser> r = new ConcurrentSkipListSet<>();

        getRoles().forEach(a -> {
            getUsersOf(a).forEach(act -> {
                if (r.contains(act)) return;
                r.add(act);
            });
        });

        return r;
    }

    public boolean hasUser(StreamlineUser user) {
        for (SavableGroupRole role : getRoles()) {
            if (roleHasUser(role, user)) return true;
        }
        return false;
    }

    public boolean roleHasUser(SavableGroupRole role, StreamlineUser user) {
        AtomicBoolean r = new AtomicBoolean(false);

        getUsersOf(role).forEach(a -> {
            if (a.getUuid().equals(user.getUuid())) r.set(true);
        });

        return r.get();
    }

    public void removeUserAll(StreamlineUser user) {
        removeUserAll(user.getUuid());
    }

    public void removeUserAll(String uuid) {
        roles.forEach((role, users) -> users.removeIf(a -> a.getUuid().equals(uuid)));
    }

    public ConcurrentSkipListSet<SavableGroupRole> getRoles() {
        return new ConcurrentSkipListSet<>(roles.keySet());
    }

    public TreeMap<Float, SavableGroupRole> getRolesOrdered() {
        TreeMap<Float, SavableGroupRole> r = new TreeMap<>();

        for (SavableGroupRole role : getRoles()) {
            float num = role.getPriority();
            while (r.containsKey(num)) {
                num += 0.001;
            }
            r.put(num, role);
        }

        return r;
    }

    public Float getPriorityOfRole(SavableGroupRole role) {
        for (Float f : getRolesOrdered().keySet()) {
            if (getRolesOrdered().get(f).equals(role)) return f;
        }
        return null;
    }

    public SavableGroupRole getRoleOf(StreamlineUser user) {
        for (SavableGroupRole role : getRoles()) {
            if (roleHasUser(role, user)) return role;
        }
        return null;
    }

    public SavableGroupRole getNextRoleOf(StreamlineUser user) {
        SavableGroupRole current = getRoleOf(user);
        if (current.equals(getRolesOrdered().lastEntry().getValue())) return null;

        return getRolesOrdered().higherEntry(getPriorityOfRole(current)).getValue();
    }

    public SavableGroupRole getPreviousRoleOf(StreamlineUser user) {
        SavableGroupRole current = getRoleOf(user);
        if (current.equals(getRolesOrdered().firstEntry().getValue())) return null;

        return getRolesOrdered().lowerEntry(getPriorityOfRole(current)).getValue();
    }

    public void addUser(StreamlineUser user) {
        promote(user);
    }

    public void promote(StreamlineUser user) {
        if (getRoleOf(user) == null) {
            applyUser(getRolesOrdered().firstEntry().getValue(), user);
            return;
        }

        if (getNextRoleOf(user) == null) return;

//        SavableGroupRole oldRole = getRoleOf(user);
        SavableGroupRole newRole = getNextRoleOf(user);

        removeUserAll(user);
        applyUser(newRole, user);
    }

    public void demote(StreamlineUser user) {
        if (getRoleOf(user) == null) applyUser(getRolesOrdered().firstEntry().getValue(), user);

//        SavableGroupRole oldRole = getRoleOf(user);
        SavableGroupRole newRole = getPreviousRoleOf(user);

        removeUserAll(user);
        applyUser(newRole, user);
    }

    public boolean userHas(StreamlineUser user, GroupFlag flag) {
        return getRoleOf(user).hasFlag(flag);
    }

    public SavableGroupRole getHigherRole(SavableGroupRole role) {
        if (! getRoles().contains(role)) return null;
        Map.Entry<Float, SavableGroupRole> entry = getRolesOrdered().higherEntry(getPriorityOfRole(role));
        if (entry == null) return null;
        return entry.getValue();
    }

    public SavableGroupRole getLowerRole(SavableGroupRole role) {
        if (! getRoles().contains(role)) return null;
        Map.Entry<Float, SavableGroupRole> entry = getRolesOrdered().lowerEntry(getPriorityOfRole(role));
        if (entry == null) return null;
        return entry.getValue();
    }

    public ConcurrentSkipListSet<SavableGroupRole> rolesAbove(SavableGroupRole role) {
        ConcurrentSkipListSet<SavableGroupRole> r = new ConcurrentSkipListSet<>();
        if (! getRoles().contains(role)) return r;

        SavableGroupRole toCheck = getHigherRole(role);
        while (toCheck != null) {
            r.add(toCheck);
            toCheck = getHigherRole(toCheck);
        }

        return r;
    }

    public ConcurrentSkipListSet<SavableGroupRole> rolesBelow(SavableGroupRole role) {
        ConcurrentSkipListSet<SavableGroupRole> r = new ConcurrentSkipListSet<>();
        if (! getRoles().contains(role)) return r;

        SavableGroupRole toCheck = getLowerRole(role);
        while (toCheck != null) {
            r.add(toCheck);
            toCheck = getLowerRole(toCheck);
        }

        return r;
    }
}
