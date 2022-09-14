package tv.quaint.listeners;

import net.streamline.api.events.EventProcessor;
import net.streamline.api.events.StreamlineEvent;
import net.streamline.api.events.StreamlineListener;
import net.streamline.api.events.server.LoginCompletedEvent;
import net.streamline.api.events.server.LogoutEvent;
import net.streamline.api.savables.users.StreamlineUser;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.GroupManager;
import tv.quaint.savable.GroupedUser;
import tv.quaint.savable.SavableGroup;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;

public class MainListener implements StreamlineListener {
    @EventProcessor
    public void updateLogin(LoginCompletedEvent event) {
        GroupedUser user = GroupManager.getOrGetGroupedUser(event.getResource().getUuid());
        GroupManager.loadGroupedUser(user);
    }

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
