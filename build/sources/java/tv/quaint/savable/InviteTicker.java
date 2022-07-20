package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.modules.BundledModule;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.SavableUser;
import net.streamline.api.scheduler.ModuleRunnable;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;

public class InviteTicker<T extends SavableGroup> extends ModuleRunnable {
    @Getter
    private final T group;
    @Getter
    private final SavableUser invited;
    @Getter
    private final SavableUser inviter;

    public InviteTicker(T group, SavableUser invited, SavableUser inviter) {
        super(StreamlineGroups.getInstance(), 0, StreamlineGroups.getConfigs().inviteTimeout());
        this.group = group;
        this.invited = invited;
        this.inviter = inviter;
    }

    @Override
    public void run() {
        group.remFromInvites(this);
        if (group instanceof SavableGuild guild) {
            ModuleUtils.sendMessage(inviter, StreamlineGroups.getMessages().guildsTimeoutInviteSender()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_leader%", guild.getMember(guild.uuid).getName())
            );
            ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().guildsTimeoutInviteOther()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_leader%", guild.getMember(guild.uuid).getName())
            );
            guild.members.forEach(a -> {
                if (a.equals(inviter)) return;
                ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsTimeoutInviteMembers()
                        .replace("%this_other%", invited.getName())
                        .replace("%this_sender%", invited.getName())
                        .replace("%this_leader%", guild.getMember(guild.uuid).getName())
                );
            });
        }
        if (group instanceof SavableParty party) {
            ModuleUtils.sendMessage(inviter, StreamlineGroups.getMessages().partiesTimeoutInviteSender()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_leader%", party.getMember(party.uuid).getName())
            );
            ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().partiesTimeoutInviteOther()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_leader%", party.getMember(party.uuid).getName())
            );
            party.members.forEach(a -> {
                if (a.equals(inviter)) return;
                ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesTimeoutInviteMembers()
                        .replace("%this_other%", invited.getName())
                        .replace("%this_sender%", invited.getName())
                        .replace("%this_leader%", party.getMember(party.uuid).getName())
                );
            });
        }

        ModuleUtils.fireEvent(new InviteTimeoutEvent<>(group, invited, inviter));

        this.cancel();
    }
}
