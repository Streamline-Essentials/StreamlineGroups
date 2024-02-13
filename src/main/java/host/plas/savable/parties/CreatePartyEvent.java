package host.plas.savable.parties;

import net.streamline.api.savables.users.StreamlineUser;
import host.plas.savable.CreateGroupEvent;

public class CreatePartyEvent extends CreateGroupEvent<SavableParty> {
    public CreatePartyEvent(SavableParty group, StreamlineUser creator) {
        super(group, creator);
    }
}
