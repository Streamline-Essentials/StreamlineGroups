package tv.quaint.savable.guilds;

import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.objects.StreamlineTitle;
import net.streamline.api.savables.users.StreamlinePlayer;
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

        LevelChangeGuildEvent event = new LevelChangeGuildEvent(this, oldL);
        ModuleUtils.fireEvent(event);

        getAllUsers().forEach(streamlineUser -> {
            if (StreamlineGroups.getConfigs().announceLevelChangeTitle()) {
                if (streamlineUser instanceof StreamlinePlayer) {
                    StreamlinePlayer player = (StreamlinePlayer) streamlineUser;
                    StreamlineTitle title = new StreamlineTitle(StreamlineGroups.getMessages().levelTitleMain(), StreamlineGroups.getMessages().levelTitleSub());
                    title.setFadeIn(StreamlineGroups.getMessages().levelTitleInTicks());
                    title.setStay(StreamlineGroups.getMessages().levelTitleStayTicks());
                    title.setFadeOut(StreamlineGroups.getMessages().levelTitleOutTicks());
                    ModuleUtils.sendTitle(player, title);
                }
            }
            if (StreamlineGroups.getConfigs().announceLevelChangeChat()) {
                StreamlineGroups.getMessages().levelChat().forEach(s -> {
                    ModuleUtils.sendMessage(streamlineUser, ModuleUtils.replaceAllPlayerBungee(streamlineUser, s));
                });
            }
        });
    }

    public void addLevel(int amount) {
        setLevel(this.level + amount);
    }

    public void removeLevel(int amount) {
        setLevel(this.level - amount);
    }

    public float getNeededXp(){
        float needed = 0;

        String function = ModuleUtils.replaceAllPlayerBungee(this.getMember(this.getUuid()), StreamlineGroups.getConfigs().guildLevelingEquation().replace("%groups_guild_level%", String.valueOf(this.level)))
                .replace("%default_level%", String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel()));

        needed = (float) MathUtils.eval(function);

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
//        StreamlineGroups.getInstance().logInfo("Set Total XP to : " + amount);

        double old = this.totalXP;

        this.totalXP = amount;

        while (xpUntilNextLevel() <= 0) {
            addLevel(1);
        }

        this.currentXP = getCurrentXP();

        ModuleUtils.fireEvent(new XPChangeGuildEvent(this, old));
    }

    public float getCurrentLevelXP(){
        float needed = 0;

        String function = ModuleUtils.replaceAllPlayerBungee(this.getMember(this.getUuid()), StreamlineGroups.getConfigs().guildLevelingEquation().replace("%groups_guild_level%", String.valueOf(this.level - 1)))
                .replace("%default_level%", String.valueOf(StreamlineGroups.getConfigs().guildStartingLevel()));

        needed = (float) MathUtils.eval(function);

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
