package tv.quaint.savable;

import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.SavableUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.flags.GroupFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GroupRoleMap {
    public SavableGroup group;
    public ConcurrentHashMap<SavableGroupRole, List<SavableUser>> roles = new ConcurrentHashMap<>();

    public GroupRoleMap(SavableGroup group) {
        this.group = group;
        get();
    }

    public List<String> getIdentifiers() {
        List<String> r = new ArrayList<>();

        for (String k : this.group.storageResource.map.keySet()) {
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
            int priority = this.group.storageResource.getOrSetDefault("roles." + a + ".priority", 1);
            int max = this.group.storageResource.getOrSetDefault("roles." + a + ".max", -1);
            String name = this.group.storageResource.getOrSetDefault("roles." + a + ".name", "&cNAME");
            List<String> flags = this.group.storageResource.getOrSetDefault("roles." + a + ".flags", new ArrayList<>());
            List<String> memberUUIDs = this.group.storageResource.getOrSetDefault("roles." + a + ".members", new ArrayList<>());

            SavableGroupRole role = new SavableGroupRole(a, priority, name, max, flags);
            List<SavableUser> users = new ArrayList<>();
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
            this.group.storageResource.write("roles." + a.getIdentifier() + ".priority", a.getPriority());
            this.group.storageResource.write("roles." + a.getIdentifier() + ".max", a.getMax());
            this.group.storageResource.write("roles." + a.getIdentifier() + ".name", a.getName());
            this.group.storageResource.write("roles." + a.getIdentifier() + ".flags", a.getFlags());

            List<String> memberUUIDs = new ArrayList<>();
            getUsersOf(a).forEach(act -> {
                memberUUIDs.add(act.uuid);
            });
            this.group.storageResource.write("roles." + a.getIdentifier() + ".members", memberUUIDs);
        });
    }

    public void addSavableGroup(SavableGroupRole role, List<SavableUser> users) {
        roles.put(role, users);
    }

    public void addSavableGroup(SavableGroupRole role) {
        addSavableGroup(role, new ArrayList<>());
    }

    public List<SavableUser> getUsersOf(SavableGroupRole role) {
        List<SavableUser> users = roles.get(role);
        if (users == null) return new ArrayList<>();
        return users;
    }

    public void applyUser(SavableGroupRole role, SavableUser user) {
        removeUserAll(user);
        List<SavableUser> users = getUsersOf(role);
        users.add(user);
    }

    public int size() {
        int r = 0;
        for (SavableGroupRole role : getRoles()) {
            r += getUsersOf(role).size();
        }
        return r;
    }

    public List<SavableUser> getAllUsers() {
        List<SavableUser> r = new ArrayList<>();

        getRoles().forEach(a -> {
            getUsersOf(a).forEach(act -> {
                if (r.contains(act)) return;
                r.add(act);
            });
        });

        return r;
    }

    public boolean hasUser(SavableUser user) {
        for (SavableGroupRole role : getRoles()) {
            if (roleHasUser(role, user)) return true;
        }
        return false;
    }

    public boolean roleHasUser(SavableGroupRole role, SavableUser user) {
        return getUsersOf(role).contains(user);
    }

    public void removeUserAll(SavableUser user) {
        roles.forEach((role, savableUsers) -> savableUsers.remove(user));
    }

    public List<SavableGroupRole> getRoles() {
        return new ArrayList<>(roles.keySet());
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

    public SavableGroupRole getRoleOf(SavableUser user) {
        for (SavableGroupRole role : getRoles()) {
            if (roleHasUser(role, user)) return role;
        }
        return null;
    }

    public SavableGroupRole getNextRoleOf(SavableUser user) {
        SavableGroupRole current = getRoleOf(user);
        if (current.equals(getRolesOrdered().lastEntry().getValue())) return null;

        return getRolesOrdered().higherEntry(getPriorityOfRole(current)).getValue();
    }

    public SavableGroupRole getPreviousRoleOf(SavableUser user) {
        SavableGroupRole current = getRoleOf(user);
        if (current.equals(getRolesOrdered().firstEntry().getValue())) return null;

        return getRolesOrdered().lowerEntry(getPriorityOfRole(current)).getValue();
    }

    public void addUser(SavableUser user) {
        promote(user);
    }

    public void promote(SavableUser user) {
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

    public void demote(SavableUser user) {
        if (getRoleOf(user) == null) applyUser(getRolesOrdered().firstEntry().getValue(), user);

//        SavableGroupRole oldRole = getRoleOf(user);
        SavableGroupRole newRole = getPreviousRoleOf(user);

        removeUserAll(user);
        applyUser(newRole, user);
    }

    public boolean userHas(SavableUser user, GroupFlag flag) {
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

    public List<SavableGroupRole> rolesAbove(SavableGroupRole role) {
        List<SavableGroupRole> r = new ArrayList<>();
        if (! getRoles().contains(role)) return r;

        SavableGroupRole toCheck = getHigherRole(role);
        while (toCheck != null) {
            r.add(toCheck);
            toCheck = getHigherRole(toCheck);
        }

        return r;
    }

    public List<SavableGroupRole> rolesBelow(SavableGroupRole role) {
        List<SavableGroupRole> r = new ArrayList<>();
        if (! getRoles().contains(role)) return r;

        SavableGroupRole toCheck = getLowerRole(role);
        while (toCheck != null) {
            r.add(toCheck);
            toCheck = getLowerRole(toCheck);
        }

        return r;
    }
}
