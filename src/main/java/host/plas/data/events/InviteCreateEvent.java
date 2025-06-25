package host.plas.data.events;

import host.plas.data.Party;
import lombok.Getter;
import singularity.data.console.CosmicSender;

@Getter
public class InviteCreateEvent<T extends Party> extends InviteGroupEvent<T> {
    private final CosmicSender inviter;

    public InviteCreateEvent(T group, CosmicSender invited, CosmicSender inviter) {
        super(group, invited);
        this.inviter = inviter;
    }
}
