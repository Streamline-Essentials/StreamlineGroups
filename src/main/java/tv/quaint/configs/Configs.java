package tv.quaint.configs;

import net.streamline.api.configs.ModularizedConfig;
import tv.quaint.StreamlineGroups;
import tv.quaint.storage.StorageUtils;
import tv.quaint.storage.resources.databases.configurations.DatabaseConfig;
import tv.quaint.thebase.lib.leonhard.storage.sections.FlatFileSection;

import java.util.HashMap;

public class Configs extends ModularizedConfig {
    public Configs() {
        super(StreamlineGroups.getInstance(), "config.yml", true);
        init();
    }

    public void init() {
        savingUse();

        getOrSetDefault("groups.base.maximum", new HashMap<>());
        getOrSetDefault("groups.party.maximum", new HashMap<>());
        getOrSetDefault("groups.guild.maximum", new HashMap<>());

        inviteTimeout();

        announceLevelChangeTitle();
        announceLevelChangeChat();
        guildPayoutExperienceAmount();
        guildPayoutExperienceEvery();
        guildStartingLevel();
        guildLevelingEquation();
    }

    public StorageUtils.SupportedStorageType savingUse() {
        reloadResource();

        return getResource().getEnum("groups.saving.use", StorageUtils.SupportedStorageType.class);
    }

    public DatabaseConfig getConfiguredDatabase() {
        FlatFileSection section = getResource().getSection("groups.saving.database");

        StorageUtils.SupportedDatabaseType type = StorageUtils.SupportedDatabaseType.valueOf(section.getOrSetDefault("type", StorageUtils.SupportedDatabaseType.SQLITE.toString()));
        String link;
        switch (type) {
            case MONGO:
                link = section.getOrDefault("link", "mongodb://{{user}}:{{pass}}@{{host}}:{{port}}/{{database}}");
                break;
            case MYSQL:
                link = section.getOrDefault("link", "jdbc:mysql://{{host}}:{{port}}/{{database}}{{options}}");
                break;
            case SQLITE:
                link = section.getOrDefault("link", "jdbc:sqlite:{{database}}.db");
                break;
            default:
                link = section.getOrSetDefault("link", "jdbc:sqlite:{{database}}.db");
                break;
        }
        String host = section.getOrSetDefault("host", "localhost");
        int port = section.getOrSetDefault("port", 3306);
        String username = section.getOrSetDefault("username", "user");
        String password = section.getOrSetDefault("password", "pass1234");
        String database = section.getOrSetDefault("database", "streamline");
        String tablePrefix = section.getOrSetDefault("table-prefix", "sl_");
        String options = section.getOrSetDefault("options", "?useSSL=false&serverTimezone=UTC");

        return new DatabaseConfig(type, link, host, port, username, password, database, tablePrefix, options);
    }

    public int baseMax(String group) {
        reloadResource();

        if (! getResource().singleLayerKeySet("groups.base.maximum").contains(group)) group = "default";
        if (! getResource().singleLayerKeySet("groups.base.maximum").contains(group)) return 8;

        return getResource().getInt("groups.base.maximum." + group);
    }

    public long inviteTimeout() {
        reloadResource();

        return getResource().getLong("groups.base.invites.timeout");
    }

    public int partyMax(String group) {
        reloadResource();

        if (! getResource().singleLayerKeySet("groups.party.maximum").contains(group)) group = "default";
        if (! getResource().singleLayerKeySet("groups.party.maximum").contains(group)) return 8;
        if (getResource().getInt("groups.party.maximum." + group) == -1) baseMax(group);

        return getResource().getInt("groups.party.maximum." + group);
    }

    public int guildMax(String group) {
        reloadResource();

        if (! getResource().singleLayerKeySet("groups.guild.maximum").contains(group)) group = "default";
        if (! getResource().singleLayerKeySet("groups.guild.maximum").contains(group)) return 8;
        if (getResource().getInt("groups.guild.maximum." + group) == -1) baseMax(group);

        return getResource().getInt("groups.guild.maximum." + group);
    }

    public boolean announceLevelChangeTitle() {
        reloadResource();

        return getResource().getBoolean("groups.guild.experience.announce.level-change.title");
    }

    public boolean announceLevelChangeChat() {
        reloadResource();

        return getResource().getBoolean("groups.guild.experience.announce.level-change.chat");
    }

    public double guildPayoutExperienceAmount() {
        reloadResource();

        return getResource().getOrSetDefault("groups.guild.experience.payout.amount", 1.0D);
    }

    public int guildPayoutExperienceEvery() {
        reloadResource();

        return getResource().getOrSetDefault("groups.guild.experience.payout.every", 400);
    }

    public int guildStartingLevel() {
        reloadResource();

        return getResource().getOrSetDefault("groups.guild.experience.starting.level", 1);
    }

    public double guildStartingExperienceAmount() {
        reloadResource();

        return getResource().getOrSetDefault("groups.guild.experience.starting.xp", 0.0D);
    }

    public String guildLevelingEquation() {
        reloadResource();

        return getResource().getString("groups.guild.experience.equation");
    }

    public StorageUtils.SupportedStorageType getSavingUseUsers() {
        reloadResource();

        return getResource().getEnum("groups.saving.use", StorageUtils.SupportedStorageType.class);
    }

    public DatabaseConfig getConfiguredDatabaseUsers() {
        FlatFileSection section = getResource().getSection("groups.saving.database");

        StorageUtils.SupportedDatabaseType type = StorageUtils.SupportedDatabaseType.valueOf(section.getOrSetDefault("type", StorageUtils.SupportedDatabaseType.SQLITE.toString()));
        String link;
        switch (type) {
            case MONGO:
                link = section.getOrDefault("link", "mongodb://{{user}}:{{pass}}@{{host}}:{{port}}/{{database}}");
                break;
            case MYSQL:
                link = section.getOrDefault("link", "jdbc:mysql://{{host}}:{{port}}/{{database}}{{options}}");
                break;
            case SQLITE:
                link = section.getOrDefault("link", "jdbc:sqlite:{{database}}.db");
                break;
            default:
                link = section.getOrSetDefault("link", "jdbc:sqlite:{{database}}.db");
                break;
        }
        String host = section.getOrSetDefault("host", "localhost");
        int port = section.getOrSetDefault("port", 3306);
        String username = section.getOrSetDefault("username", "user");
        String password = section.getOrSetDefault("password", "pass1234");
        String database = section.getOrSetDefault("database", "streamline");
        String tablePrefix = section.getOrSetDefault("table-prefix", "sl_");
        String options = section.getOrSetDefault("options", "?useSSL=false&serverTimezone=UTC");

        return new DatabaseConfig(type, link, host, port, username, password, database, tablePrefix, options);
    }
}
