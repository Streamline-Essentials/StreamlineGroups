package tv.quaint.listeners;

import net.streamline.api.events.StreamlineEvent;
import net.streamline.api.events.StreamlineEventBus;
import net.streamline.api.events.server.LoginEvent;
import net.streamline.api.modules.BundledModule;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;

public class MainListener extends StreamlineEventBus.ModularizedObserver {
    public MainListener(BundledModule module) {
        super(module);
    }

    @Override
    public void update(StreamlineEvent<?> streamlineEvent) {
        if (streamlineEvent instanceof LoginEvent event) {
            GroupedUser user = GroupManager.getOrGetGroupedUser(event.getResource().uuid);
            GroupManager.loadGroupedUser(user);
        }
    }
}
