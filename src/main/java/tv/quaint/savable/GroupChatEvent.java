package tv.quaint.savable;

import lombok.Getter;
import lombok.Setter;
import net.streamline.api.savables.users.StreamlineUser;

public class GroupChatEvent<T extends SavableGroup> extends GroupEvent<T> {
    @Getter @Setter
    private String message;
    @Getter @Setter
    private StreamlineUser sender;

    public GroupChatEvent(T group, StreamlineUser sender, String message) {
        super(group);
        setSender(sender);
        setMessage(message);
    }
}
