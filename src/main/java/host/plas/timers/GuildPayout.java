package host.plas.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import host.plas.StreamlineGroups;
import host.plas.savable.GroupManager;
import host.plas.savable.SavableGroup;
import host.plas.savable.guilds.SavableGuild;

public class GuildPayout extends ModuleRunnable {
    public GuildPayout() {
        super(StreamlineGroups.getInstance(), 0, 400L);
    }

    @Override
    public void run() {
        for (SavableGroup group : GroupManager.getGroupsOf(SavableGuild.class)) {
            if (! (group instanceof SavableGuild)) continue;
            SavableGuild guild = (SavableGuild) group;
            guild.addTotalXP(StreamlineGroups.getConfigs().guildPayoutExperienceAmount());
        }
    }
}
