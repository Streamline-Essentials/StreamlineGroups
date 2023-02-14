package tv.quaint.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;

public class GroupSyncer extends ModuleRunnable {
    public GroupSyncer() {
        super(StreamlineGroups.getInstance(), 0, 6000);
    }

    @Override
    public void run() {
        GroupManager.syncAllUsers();
        GroupManager.syncAllGroups();
    }
}
