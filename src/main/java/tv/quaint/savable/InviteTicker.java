package tv.quaint.savable;

import lombok.Getter;
import net.streamline.api.modules.ModuleUtils;
import net.streamline.api.savables.users.StreamlineUser;
import net.streamline.api.scheduler.ModuleRunnable;
import org.jetbrains.annotations.NotNull;
import tv.quaint.StreamlineGroups;
import tv.quaint.savable.guilds.SavableGuild;
import tv.quaint.savable.parties.SavableParty;

public class InviteTicker<T extends SavableGroup> extends ModuleRunnable implements Comparable<InviteTicker<?>> {
    @Getter
    private final T group;
    @Getter
    private final StreamlineUser invited;
    @Getter
    private final StreamlineUser inviter;

    public InviteTicker(T group, StreamlineUser invited, StreamlineUser inviter) {
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
                    .replace("%this_owner%", guild.getMember(guild.getUuid()).getName())
            );
            ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().guildsTimeoutInviteOther()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_owner%", guild.getMember(guild.getUuid()).getName())
            );
            guild.getAllUsers().forEach(a -> {
                if (a.equals(inviter)) return;
                ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().guildsTimeoutInviteMembers()
                        .replace("%this_other%", invited.getName())
                        .replace("%this_sender%", invited.getName())
                        .replace("%this_owner%", guild.getMember(guild.getUuid()).getName())
                );
            });
        }
        if (group instanceof SavableParty party) {
            ModuleUtils.sendMessage(inviter, StreamlineGroups.getMessages().partiesTimeoutInviteSender()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_owner%", party.getMember(party.getUuid()).getName())
            );
            ModuleUtils.sendMessage(invited, StreamlineGroups.getMessages().partiesTimeoutInviteOther()
                    .replace("%this_other%", invited.getName())
                    .replace("%this_sender%", invited.getName())
                    .replace("%this_owner%", party.getMember(party.getUuid()).getName())
            );
            party.getAllUsers().forEach(a -> {
                if (a.equals(inviter)) return;
                ModuleUtils.sendMessage(a, StreamlineGroups.getMessages().partiesTimeoutInviteMembers()
                        .replace("%this_other%", invited.getName())
                        .replace("%this_sender%", invited.getName())
                        .replace("%this_owner%", party.getMember(party.getUuid()).getName())
                );
            });
        }

        ModuleUtils.fireEvent(new InviteTimeoutEvent<>(group, invited, inviter));

        this.cancel();
    }

    @Override
    public int compareTo(@NotNull InviteTicker<?> o) {
        return Integer.compare(getIndex(), o.getIndex());
    }
}
