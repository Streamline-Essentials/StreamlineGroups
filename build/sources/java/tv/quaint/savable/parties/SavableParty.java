package tv.quaint.savable.parties;

import net.streamline.api.savables.SavableResource;
import net.streamline.api.savables.users.SavableUser;
import tv.quaint.savable.SavableGroup;

public class SavableParty extends SavableGroup {
    public SavableParty(SavableUser leader) {
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
