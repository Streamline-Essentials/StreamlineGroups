package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.savables.users.SavableUser;

public class InviteGroupEvent<T extends SavableGroup> extends GroupEvent<T> {
    @Getter
    private final SavableUser invited;

    public InviteGroupEvent(T group, SavableUser invited) {
        super(group);
        this.invited = invited;
    }
}
