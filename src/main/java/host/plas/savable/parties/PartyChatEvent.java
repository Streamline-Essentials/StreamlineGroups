package host.plas.savable.parties;

import net.streamline.api.savables.users.StreamlineUser;
import host.plas.savable.GroupChatEvent;

public class PartyChatEvent extends GroupChatEvent<SavableParty> {
    public PartyChatEvent(SavableParty group, StreamlineUser sender, String message) {
        super(group, sender, message);
    }
}
