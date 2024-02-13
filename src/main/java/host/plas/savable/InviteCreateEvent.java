package host.plas.savable;

import lombok.Getter;
import net.streamline.api.savables.users.StreamlineUser;

public class InviteCreateEvent<T extends SavableGroup> extends InviteGroupEvent<T> {
    @Getter
    private final StreamlineUser inviter;

    public InviteCreateEvent(T group, StreamlineUser invited, StreamlineUser inviter) {
        super(group, invited);
        this.inviter = inviter;
    }
}
