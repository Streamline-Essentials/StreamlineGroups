package host.plas;

import host.plas.commands.GCCommand;
import host.plas.commands.GuildCommand;
import host.plas.commands.PCCommand;
import host.plas.commands.PartyCommand;
import host.plas.configs.Configs;
import host.plas.configs.DefaultRoles;
import host.plas.configs.Messages;
import host.plas.listeners.MainListener;
import host.plas.placeholders.GroupsExpansion;
import host.plas.savable.GroupManager;
import host.plas.savable.GroupedUser;
import host.plas.savable.guilds.SavableGuild;
import host.plas.savable.parties.SavableParty;
import host.plas.timers.GroupSaver;
import host.plas.timers.GroupSyncer;
import host.plas.timers.GuildPayout;
import host.plas.timers.UserSaver;
import lombok.Getter;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.SimpleModule;
import net.streamline.api.utils.UserUtils;
import net.streamline.thebase.lib.pf4j.PluginWrapper;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListSet;

public class StreamlineGroups extends SimpleModule {
    @Getter
    static StreamlineGroups instance;

    @Getter
    static File usersFolder;
    @Getter
    static File groupsFolder;

    @Getter
    static Configs configs;
    @Getter
    static Messages messages;
    @Getter
    static DefaultRoles defaultRoles;

    @Getter
    static GroupSaver groupSaver;
    @Getter
    static UserSaver userSaver;
    @Getter
    static GuildPayout guildPayout;

    @Getter
    static MainListener mainListener;

    @Getter
    static GroupsExpansion groupsExpansion;

    public StreamlineGroups(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void onLoad() {
        instance = this;
        groupsExpansion = new GroupsExpansion();
    }

    @Override
    public void onEnable() {
        configs = new Configs();
        messages = new Messages();
        defaultRoles = new DefaultRoles();

        groupSaver = new GroupSaver();
        userSaver = new UserSaver();
        guildPayout = new GuildPayout();

        new GroupSyncer();

        GroupManager.registerClass(SavableGuild.class,  c -> {
            SavableGuild guild = GroupManager.getOrGetGuild(c);
            if (guild == null) return;
            GroupManager.loadGroup(guild);
        });
        GroupManager.registerClass(SavableParty.class, c -> {
            SavableParty party = GroupManager.getOrGetParty(c);
            if (party == null) return;
            GroupManager.loadGroup(party);
        });

        usersFolder = new File(getDataFolder(), "users" + File.separator);
        groupsFolder = new File(getDataFolder(), "groups" + File.separator);
        usersFolder.mkdirs();
        groupsFolder.mkdirs();

        mainListener = new MainListener();
        ModuleUtils.listen(mainListener, this);

        getGroupsExpansion().init();

        GroupManager.getOrGetGroupedUser(UserUtils.getConsole().getUuid());

        UserUtils.getLoadedUsersSet().forEach(a -> {
            GroupedUser user = GroupManager.getOrGetGroupedUser(a.getUuid());
            GroupManager.loadGroupedUser(user);
        });

        new GuildCommand(this).register();
        new PartyCommand(this).register();
        new GCCommand().register();
        new PCCommand().register();
    }

    @Override
    public void onDisable() {
        GroupManager.getLoadedGroups().forEach((clazz, savableGroups) -> {
            savableGroups.forEach(savableGroup -> {
                savableGroup.saveAll();
                savableGroup.getStorageResource().push();
                GroupManager.syncGroup(savableGroup);
            });
            GroupManager.getLoadedGroups().put(clazz, new ConcurrentSkipListSet<>());
        });
        GroupManager.getLoadedGroupedUsers().forEach(groupedUser -> {
            groupedUser.saveAll();
            groupedUser.getStorageResource().push();
            GroupManager.syncUser(groupedUser);
        });
        GroupManager.setLoadedGroupedUsers(new ConcurrentSkipListSet<>());
        getGroupsExpansion().stop();
    }
}
