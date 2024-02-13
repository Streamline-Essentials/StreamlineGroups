package host.plas.listeners;

import net.streamline.api.events.server.LoginCompletedEvent;
import net.streamline.api.events.server.LogoutEvent;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.events.BaseEventListener;
import tv.quaint.events.processing.BaseProcessor;
import host.plas.savable.GroupManager;
import host.plas.savable.GroupedUser;
import host.plas.savable.guilds.SavableGuild;
import host.plas.savable.parties.SavableParty;

public class MainListener implements BaseEventListener {
    @BaseProcessor
    public void updateLogin(LoginCompletedEvent event) {
        GroupedUser user = GroupManager.getOrGetGroupedUser(event.getResource().getUuid());
        GroupManager.loadGroupedUser(user);
        SavableGuild guild = user.getGroup(SavableGuild.class);
        SavableParty party = user.getGroup(SavableParty.class);

        if (guild != null) {
            if (! GroupManager.isLoaded(guild.getUuid(), SavableGuild.class)) {
                GroupManager.loadGroup(guild);
            }
        }
        if (party != null) {
            if (! GroupManager.isLoaded(party.getUuid(), SavableParty.class)) {
                GroupManager.loadGroup(party);
            }
        }
    }

    @BaseProcessor
    public void updateLogout(LogoutEvent event) {
        GroupedUser user = GroupManager.getOrGetGroupedUser(event.getResource().getUuid());
        if (user.hasGroup(SavableParty.class)) {
            SavableParty party = user.getGroup(SavableParty.class);
            if (! areAnyOnline(party.getAllUsers().toArray(new StreamlineUser[0]))) {
                party.saveAll();
                GroupManager.removeGroupOf(party);
            }
        }

        GroupManager.getRegisteredClasses().values().forEach(a -> {
            if (user.hasGroup(a)) {
                SavableGuild guild = (SavableGuild) user.getGroup(a);
                if (! areAnyOnline(guild.getAllUsers().toArray(new StreamlineUser[0]))) {
                    guild.saveAll();
                    GroupManager.removeGroupOf(guild);
                }
            }
        });
    }

    public boolean areAnyOnline(StreamlineUser... users) {
        for (StreamlineUser user : users) {
            if (user.isOnline()) return true;
        }

        return false;
    }
}
