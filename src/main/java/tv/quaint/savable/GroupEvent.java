package tv.quaint.savable;

import net.streamline.api.savables.events.SavableEvent;
import tv.quaint.StreamlineGroups;

public class GroupEvent<T extends SavableGroup> extends SavableEvent<T> {
    public GroupEvent(T group) {
        super(group);
    }
}
