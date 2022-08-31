package tv.quaint.savable.guilds;

import net.streamline.api.configs.StorageResource;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.StreamlineUser;
import net.streamline.api.utils.MathUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.SavableGroup;

public class SavableGuild extends SavableGroup {
    public String name;
    public double totalXP;
    public double currentXP;
    public int level;

    public SavableGuild(StreamlineUser leader) {
        super(leader, SavableGuild.class);
    }

    public SavableGuild(String uuid) {
        super(uuid, SavableGuild.class);
    }

    @Override
    public void populateMoreDefaults() {
        name = getOrSetDefault("guild.name", "");
        totalXP = getOrSetDefault("guild.stats.experience.total", StreamlineGroups.getConfigs().guildStartingExperienceAmount());
        currentXP = getOrSetDefault("guild.stats.experience.current", StreamlineGroups.getConfigs().guildStartingExperienceAmount());
        level = getOrSetDefault("guild.stats.level", StreamlineGroups.getConfigs().guildStartingLevel());
    }

    @Override
    public void loadMoreValues() {
        name = getOrSetDefault("guild.name", name);
        totalXP = getOrSetDefault("guild.stats.experience.total", totalXP);
        currentXP = getOrSetDefault("guild.stats.experience.current", currentXP);
        level = getOrSetDefault("guild.stats.level", level);
    }

    @Override
    public void saveMore() {
        set("guild.name", name);
        set("guild.stats.experience.total", totalXP);
        set("guild.stats.experience.current", currentXP);
        set("guild.stats.level", level);
    }

    public void setLevel(int amount) {
        int oldL = this.level;

        this.level = amount;

        ModuleUtils.fireEvent(new LevelChangeGuildEvent(this, oldL));
    }

    public void addLevel(int amount) {
        setLevel(this.level + amount);
    }

    public void removeLevel(int amount) {
        setLevel(this.level - amount);
    }

    public double getNeededXp(){
        double needed = 0;

        String function = ModuleUtils.replaceAllPlayerBungee(this.getMember(this.uuid), StreamlineGroups.getConfigs().guildLevelingEquation())
                .replace("%default_level%", String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel()));

        needed = (double) MathUtils.eval(function);

        return needed;
    }

    public double xpUntilNextLevel(){
        return getNeededXp() - this.totalXP;
    }

    public void addTotalXP(double amount){
        setTotalXP(this.totalXP + amount);
    }

    public void removeTotalXP(double amount){
        setTotalXP(this.totalXP - amount);
    }

    public void setTotalXP(double amount){
        double old = this.totalXP;

        this.totalXP = amount;

        while (xpUntilNextLevel() <= 0) {
            addLevel(1);
        }

        this.currentXP = getCurrentXP();

        ModuleUtils.fireEvent(new XPChangeGuildEvent(this, old));
    }

    public double getCurrentLevelXP(){
        double needed = 0;

        String function = ModuleUtils.replaceAllPlayerBungee(this.getMember(this.uuid), StreamlineGroups.getConfigs().guildLevelingEquation().replace("%groups_guild_level%", String.valueOf(this.level - 1)))
                .replace("%default_level%", String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel()));

        needed = (double) MathUtils.eval(function);

        return needed;
    }

    public double getCurrentXP(){
        //        loadValues();
        return this.totalXP - getCurrentLevelXP();
    }

    public String setNameReturnOld(String newName) {
        String toReturn = this.name;
        this.name = newName;
        return toReturn;
    }
}
