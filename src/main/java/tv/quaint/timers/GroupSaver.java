package tv.quaint.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.SavableGroup;

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
