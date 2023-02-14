package tv.quaint;

import lombok.Getter;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.SimpleModule;
import net.streamline.api.utils.UserUtils;
import tv.quaint.commands.GuildCommand;
import tv.quaint.commands.PartyCommand;
import tv.quaint.configs.Configs;
import tv.quaint.configs.DefaultRoles;
import tv.quaint.configs.Messages;
import tv.quaint.listeners.MainListener;
import tv.quaint.placeholders.GroupsExpansion;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;
import tv.quaint.thebase.lib.pf4j.PluginWrapper;
import tv.quaint.timers.GroupSaver;
import tv.quaint.timers.GroupSyncer;
import tv.quaint.timers.GuildPayout;
import tv.quaint.timers.UserSaver;

import java.io.File;
import java.util.List;
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
    public void registerCommands() {
        setCommands(List.of(
                new GuildCommand(this),
                new PartyCommand(this)
        ));
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
