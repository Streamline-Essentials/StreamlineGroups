package host.plas.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import host.plas.savable.GroupedUser;
import host.plas.savable.SavableGroup;

public class UserSaver extends ModuleRunnable {
    public UserSaver() {
        super(StreamlineGroups.getInstance(), 0L, 400L);
    }

    @Override
    public void run() {
        GroupManager.getLoadedGroupedUsers().forEach(GroupedUser::saveAll);
    }
}
