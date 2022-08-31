package tv.quaint.savable.guilds;

import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.savable.CreateGroupEvent;

public class CreateGuildEvent extends CreateGroupEvent<SavableGuild> {
    public CreateGuildEvent(SavableGuild group, StreamlineUser creator) {
        super(group, creator);
    }
}
