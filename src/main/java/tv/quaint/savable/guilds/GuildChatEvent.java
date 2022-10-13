package tv.quaint.savable.guilds;

import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.savable.GroupChatEvent;

public class GuildChatEvent extends GroupChatEvent<SavableGuild> {
    public GuildChatEvent(SavableGuild group, StreamlineUser sender, String message) {
        super(group, sender, message);
    }
}
