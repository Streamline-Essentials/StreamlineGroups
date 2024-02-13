package host.plas.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;

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
