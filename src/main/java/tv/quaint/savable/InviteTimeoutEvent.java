package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.savables.users.StreamlineUser;

public class InviteTimeoutEvent<T extends SavableGroup> extends InviteGroupEvent<T> {
    @Getter
    private final StreamlineUser inviter;
    public InviteTimeoutEvent(T group, StreamlineUser invited, StreamlineUser inviter) {
        super(group, invited);
        this.inviter = inviter;
    }
}
