package tv.quaint.savable.parties;

import net.streamline.api.savables.users.SavableUser;
import tv.quaint.savable.CreateGroupEvent;

public class CreatePartyEvent extends CreateGroupEvent<SavableParty> {
    public CreatePartyEvent(SavableParty group, SavableUser creator) {
        super(group, creator);
    }
}
