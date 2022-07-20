package tv.quaint.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.SavableGroup;

public class UserSaver extends ModuleRunnable {
    public UserSaver() {
        super(StreamlineGroups.getInstance(), 0L, 400L);
    }

    @Override
    public void run() {
        for (GroupedUser user : GroupManager.getLoadedGroupedUsers()) {
            user.saveAll();
        }
    }
}
