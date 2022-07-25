package tv.quaint.listeners;

import net.streamline.api.events.EventProcessor;
import net.streamline.api.events.StreamlineEvent;
import net.streamline.api.events.StreamlineListener;
import net.streamline.api.events.server.LoginEvent;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;

public class MainListener implements StreamlineListener {
    @EventProcessor
    public void update(LoginEvent event) {
        GroupedUser user = GroupManager.getOrGetGroupedUser(event.getResource().uuid);
        GroupManager.loadGroupedUser(user);
    }
}
