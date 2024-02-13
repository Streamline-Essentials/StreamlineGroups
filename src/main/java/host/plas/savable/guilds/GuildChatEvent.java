package host.plas.savable.guilds;

import net.streamline.api.savables.users.StreamlineUser;
import host.plas.savable.GroupChatEvent;

public class GuildChatEvent extends GroupChatEvent<SavableGuild> {
    public GuildChatEvent(SavableGuild group, StreamlineUser sender, String message) {
        super(group, sender, message);
    }
}
