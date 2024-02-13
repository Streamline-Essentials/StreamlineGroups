package host.plas.savable;

import net.streamline.api.savables.events.SavableEvent;
import host.plas.StreamlineGroups;

public class GroupEvent<T extends SavableGroup> extends SavableEvent<T> {
    public GroupEvent(T group) {
        super(group);
    }
}
