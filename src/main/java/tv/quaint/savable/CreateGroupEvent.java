package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.savables.users.StreamlineUser;

public class CreateGroupEvent<T extends SavableGroup> extends GroupEvent<T> {
    @Getter
    private final StreamlineUser creator;

    public CreateGroupEvent(T group, StreamlineUser creator) {
        super(group);
        this.creator = creator;
    }
}
