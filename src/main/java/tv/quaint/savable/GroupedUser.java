package tv.quaint.savable;

import de.leonhard.storage.internal.FileData;
import de.leonhard.storage.internal.settings.DataType;
import lombok.Getter;
import net.streamline.api.configs.StorageResource;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;

import java.util.concurrent.ConcurrentHashMap;

public class GroupedUser extends SavableResource {
    @Override
    public StorageResource<?> getStorageResource() {
        return storageResource;
    }

    @Getter
    private ConcurrentHashMap<Class<? extends SavableGroup>, String> associatedGroups = new ConcurrentHashMap<>();

    public GroupedUser(String uuid) {
        this(uuid, true);
    }

    public GroupedUser(String uuid, boolean load) {
        super(uuid, GroupManager.newStorageResourceUsers(uuid, GroupedUser.class));
        if (load) loadAfter();
    }

    @Override
    public void populateDefaults() {
        if (associatedGroups == null) return;
        associatedGroups.forEach((a, b) -> {
            storageResource.getOrSetDefault("associated." + a.getSimpleName(), b);
        });
        storageResource.sync();
    }

    @Override
    public void loadValues() {
    }

    public void loadAfter() {
        if (this.associatedGroups == null) this.associatedGroups = new ConcurrentHashMap<>();
        storageResource.reloadResource(true);

        for (String clazz : GroupManager.getRegisteredClasses().keySet()) {
            String uuid = storageResource.getOrSetDefault("associated." + clazz, "null");
            if (uuid == null) continue;
            if (uuid.equals("null")) continue;
            associatedGroups.put(GroupManager.getRegisteredClass(clazz), uuid);
        }

        loadAllAssociatedGroups();
    }

    @Override
    public void saveAll() {
        if (associatedGroups == null) return;
        associatedGroups.forEach((a, b) -> {
            storageResource.write("associated." + a.getSimpleName(), b);
        });
        storageResource.sync();
    }

    public void associateWith(Class<? extends SavableGroup> groupType, String uuidOfGroup) {
        getAssociatedGroups().put(groupType, uuidOfGroup);
        saveAll();
    }

    public void disassociateWith(Class<? extends SavableGroup> groupType, String uuidOfGroup) {
        getAssociatedGroups().put(groupType, "null");
        saveAll();
    }

    public <T extends SavableGroup> T getGroup(Class<T> type) {
        String uuidOfGroup = getAssociatedGroups().get(type);
        if (uuidOfGroup == null) return null;

        return GroupManager.getGroup(type, uuidOfGroup);
    }

    public void loadAllAssociatedGroups() {
        if (associatedGroups == null) {
            associatedGroups = new ConcurrentHashMap<>();
        }
        for (Class<? extends SavableGroup> clazz : getAssociatedGroups().keySet()) {
            GroupManager.getRegisteredLoadOrders().get(clazz).accept(getAssociatedGroups().get(clazz));
        }
    }

    public boolean hasGroup(Class<? extends SavableGroup> clazz) {
        if (! getAssociatedGroups().containsKey(clazz) || getAssociatedGroups().get(clazz) != null) {
            return false;
        }
        SavableGroup group = GroupManager.getGroup(clazz, getAssociatedGroups().get(clazz));
        if (group == null) return false;
        return group.hasMember(asUser());
    }

    public StreamlineUser asUser() {
        return ModuleUtils.getOrGetUser(this.uuid);
    }
}
