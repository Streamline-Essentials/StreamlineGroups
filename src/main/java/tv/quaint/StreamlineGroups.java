package tv.quaint;

import lombok.Getter;
import net.streamline.api.command.ModuleCommand;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.modules.SimpleModule;
import net.streamline.api.modules.dependencies.Dependency;
import net.streamline.api.placeholder.RATExpansion;
import tv.quaint.commands.GuildCommand;
import tv.quaint.commands.PartyCommand;
import tv.quaint.configs.Configs;
import tv.quaint.configs.DefaultRoles;
import tv.quaint.configs.Messages;
import tv.quaint.listeners.MainListener;
import tv.quaint.placeholders.GroupsExpansion;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.SavableGroup;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;
import tv.quaint.timers.GroupSaver;
import tv.quaint.timers.GuildPayout;
import tv.quaint.timers.UserSaver;

import java.io.File;
import java.util.Collections;
import java.util.List;

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

    @Override
    public String identifier() {
        return "streamline-groups";
    }

    @Override
    public List<String> authors() {
        return List.of("Quaint");
    }

    @Override
    public List<Dependency> dependencies() {
        return Collections.emptyList();
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

        getGroupsExpansion().register();
    }

    @Override
    public void onDisable() {
        GroupManager.getLoadedGroups().forEach((clazz, savableGroups) -> {
            savableGroups.forEach(SavableGroup::saveAll);
        });
        GroupManager.getLoadedGroupedUsers().forEach(GroupedUser::saveAll);
        getGroupsExpansion().unregister();
    }
}
