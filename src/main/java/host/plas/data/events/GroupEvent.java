package host.plas.data.events;

import host.plas.data.Party;
import lombok.Getter;
import lombok.Setter;
import singularity.events.CosmicEvent;

@Getter @Setter
public class GroupEvent<T extends Party> extends CosmicEvent {
    private T group;

    public GroupEvent(T group) {
        this.group = group;
    }
}
