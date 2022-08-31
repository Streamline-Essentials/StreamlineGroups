package tv.quaint.savable.guilds;

import lombok.Getter;

public class XPChangeGuildEvent extends ExperienceSavableGuildEvent {
    @Getter
    private final double oldAmount;

    public XPChangeGuildEvent(SavableGuild guild, double oldAmount) {
        super(guild);
        this.oldAmount = oldAmount;
    }
}
