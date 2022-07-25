package tv.quaint.timers;

import net.streamline.api.scheduler.ModuleRunnable;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.SavableGroup;
import tv.quaint.savable.guilds.SavableGuild;

public class GuildPayout extends ModuleRunnable {
    public GuildPayout() {
        super(StreamlineGroups.getInstance(), 0, StreamlineGroups.getConfigs().guildPayoutExperienceEvery());
    }

    @Override
    public void run() {
        for (SavableGroup group : GroupManager.getGroupsOf(SavableGuild.class)) {
            if (! (group instanceof SavableGuild guild)) continue;
            guild.addTotalXP(StreamlineGroups.getConfigs().guildPayoutExperienceAmount());
        }
    }
}
