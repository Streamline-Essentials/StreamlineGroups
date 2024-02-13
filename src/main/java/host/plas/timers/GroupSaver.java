package host.plas.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import host.plas.savable.GroupedUser;
import host.plas.savable.SavableGroup;

public class GroupSaver extends ModuleRunnable {
    public GroupSaver() {
        super(StreamlineGroups.getInstance(), 0L, 2400L);
    }

    @Override
    public void run() {
        for (Class<? extends SavableGroup> groupClass : GroupManager.getLoadedGroups().keySet()) {
            for (SavableGroup group : GroupManager.getLoadedGroups().get(groupClass)) {
                group.saveAll();
            }
        }
    }
}
