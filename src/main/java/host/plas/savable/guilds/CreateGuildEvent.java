package host.plas.savable.guilds;

import net.streamline.api.savables.users.StreamlineUser;
import host.plas.savable.CreateGroupEvent;

public class CreateGuildEvent extends CreateGroupEvent<SavableGuild> {
    public CreateGuildEvent(SavableGuild group, StreamlineUser creator) {
        super(group, creator);
    }
}
