package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.savables.users.SavableUser;

public class InviteTimeoutEvent<T extends SavableGroup> extends InviteGroupEvent<T> {
    @Getter
    private final SavableUser inviter;
    public InviteTimeoutEvent(T group, SavableUser invited, SavableUser inviter) {
        super(group, invited);
        this.inviter = inviter;
    }
}
