package tv.quaint.savable.parties;

import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.savable.SavableGroup;

public class SavableParty extends SavableGroup {
    public SavableParty(StreamlineUser leader) {
        super(leader, SavableParty.class);
    }

    public SavableParty(String uuid) {
        super(uuid, SavableParty.class);
    }

    @Override
    public void populateMoreDefaults() {

    }

    @Override
    public void loadMoreValues() {

    }

    @Override
    public void saveMore() {

    }
}
