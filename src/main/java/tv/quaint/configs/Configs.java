package tv.quaint.configs;

import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import tv.quaint.StreamlineGroups;

public class Configs extends ModularizedConfig {
    public Configs() {
        super(StreamlineGroups.getInstance(), "config.yml", true);
    }

    public StorageUtils.StorageType savingUse() {
        reloadResource();

        return resource.getEnum("groups.saving.use", StorageUtils.StorageType.class);
    }

    public String savingUri() {
        reloadResource();

        return resource.getString("groups.saving.databases.connection-uri");
    }

    public String savingDatabase() {
        reloadResource();

        return resource.getString("groups.saving.databases.database");
    }

    public String savingPrefix() {
        reloadResource();

        return resource.getString("groups.saving.databases.prefix");
    }

    public DatabaseConfig getConfiguredDatabase() {
        StorageUtils.DatabaseType databaseType = null;
        if (savingUse().equals(StorageUtils.StorageType.MONGO)) databaseType = StorageUtils.DatabaseType.MONGO;
        if (savingUse().equals(StorageUtils.StorageType.MYSQL)) databaseType = StorageUtils.DatabaseType.MYSQL;
        if (databaseType == null) return null;

        return new DatabaseConfig(savingUri(), savingDatabase(), savingPrefix(), databaseType);
    }

    public int baseMax(String group) {
        reloadResource();

        if (! resource.singleLayerKeySet("groups.base.maximum").contains(group)) group = "default";
        if (! resource.singleLayerKeySet("groups.base.maximum").contains(group)) return 8;

        return resource.getInt("groups.base.maximum." + group);
    }

    public long inviteTimeout() {
        reloadResource();

        return resource.getLong("groups.base.invites.timeout");
    }

    public int partyMax(String group) {
        reloadResource();

        if (! resource.singleLayerKeySet("groups.party.maximum").contains(group)) group = "default";
        if (! resource.singleLayerKeySet("groups.party.maximum").contains(group)) return 8;
        if (resource.getInt("groups.party.maximum." + group) == -1) baseMax(group);

        return resource.getInt("groups.party.maximum." + group);
    }

    public int guildMax(String group) {
        reloadResource();

        if (! resource.singleLayerKeySet("groups.guild.maximum").contains(group)) group = "default";
        if (! resource.singleLayerKeySet("groups.guild.maximum").contains(group)) return 8;
        if (resource.getInt("groups.guild.maximum." + group) == -1) baseMax(group);

        return resource.getInt("groups.guild.maximum." + group);
    }

    public boolean announceLevelChangeTitle() {
        reloadResource();

        return resource.getBoolean("groups.guild.experience.announce.level-change.title");
    }

    public boolean announceLevelChangeChat() {
        reloadResource();

        return resource.getBoolean("groups.guild.experience.announce.level-change.chat");
    }

    public double guildPayoutExperienceAmount() {
        reloadResource();

        return resource.getOrSetDefault("groups.guild.experience.payout.amount", 1.0D);
    }

    public int guildPayoutExperienceEvery() {
        reloadResource();

        return resource.getOrSetDefault("groups.guild.experience.payout.every", 400);
    }

    public int guildStartingLevel() {
        reloadResource();

        return resource.getOrSetDefault("groups.guild.experience.starting.level", 1);
    }

    public double guildStartingExperienceAmount() {
        reloadResource();

        return resource.getOrSetDefault("groups.guild.experience.starting.xp", 0.0D);
    }

    public String guildLevelingEquation() {
        reloadResource();

        return resource.getString("groups.guild.experience.equation");
    }


    public StorageUtils.StorageType savingUseUsers() {
        reloadResource();

        return resource.getEnum("groups.saving.use", StorageUtils.StorageType.class);
    }

    public String savingUriUsers() {
        reloadResource();

        return resource.getString("groups.saving.databases.connection-uri");
    }

    public String savingDatabaseUsers() {
        reloadResource();

        return resource.getString("groups.saving.databases.database");
    }

    public String savingPrefixUsers() {
        reloadResource();

        return resource.getString("groups.saving.databases.prefix");
    }

    public DatabaseConfig getConfiguredDatabaseUsers() {
        StorageUtils.DatabaseType databaseType = null;
        if (savingUseUsers().equals(StorageUtils.StorageType.MONGO)) databaseType = StorageUtils.DatabaseType.MONGO;
        if (savingUseUsers().equals(StorageUtils.StorageType.MYSQL)) databaseType = StorageUtils.DatabaseType.MYSQL;
        if (databaseType == null) return null;

        return new DatabaseConfig(savingUriUsers(), savingDatabaseUsers(), savingPrefixUsers(), databaseType);
    }
}
