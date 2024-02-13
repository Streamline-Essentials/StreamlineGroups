package host.plas.savable.guilds;

import host.plas.savable.GroupEvent;
import host.plas.savable.SavableGroup;

public class GuildGroupEvent extends GroupEvent<SavableGuild> {
    public GuildGroupEvent(SavableGuild guild) {
        super(guild);
    }
}
