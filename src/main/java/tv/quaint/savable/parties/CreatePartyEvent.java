package tv.quaint.savable.parties;

import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.savable.CreateGroupEvent;

public class CreatePartyEvent extends CreateGroupEvent<SavableParty> {
    public CreatePartyEvent(SavableParty group, StreamlineUser creator) {
        super(group, creator);
    }
}
