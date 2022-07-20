package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.savables.users.SavableUser;

public class CreateGroupEvent<T extends SavableGroup> extends GroupEvent<T> {
    @Getter
    private final SavableUser creator;

    public CreateGroupEvent(T group, SavableUser creator) {
        super(group);
        this.creator = creator;
    }
}
