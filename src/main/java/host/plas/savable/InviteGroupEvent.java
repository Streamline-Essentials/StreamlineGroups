package host.plas.savable;

import lombok.Getter;
import net.streamline.api.savables.users.StreamlineUser;

public class InviteGroupEvent<T extends SavableGroup> extends GroupEvent<T> {
    @Getter
    private final StreamlineUser invited;

    public InviteGroupEvent(T group, StreamlineUser invited) {
        super(group);
        this.invited = invited;
    }
}
