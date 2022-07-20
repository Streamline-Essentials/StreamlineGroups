package tv.quaint.savable.guilds;

import net.streamline.api.savables.users.SavableUser;
import tv.quaint.savable.CreateGroupEvent;

public class CreateGuildEvent extends CreateGroupEvent<SavableGuild> {
    public CreateGuildEvent(SavableGuild group, SavableUser creator) {
        super(group, creator);
    }
}
