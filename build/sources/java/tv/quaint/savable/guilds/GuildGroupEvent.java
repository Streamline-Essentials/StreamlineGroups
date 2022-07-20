package tv.quaint.savable.guilds;

import tv.quaint.savable.GroupEvent;
import tv.quaint.savable.SavableGroup;

public class GuildGroupEvent extends GroupEvent<SavableGuild> {
    public GuildGroupEvent(SavableGuild guild) {
        super(guild);
    }
}
