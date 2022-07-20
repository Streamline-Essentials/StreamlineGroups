package tv.quaint.savable.guilds;

import lombok.Getter;

public class XPChangeGuildEvent extends ExperienceSavableGuildEvent {
    @Getter
    private final float oldAmount;

    public XPChangeGuildEvent(SavableGuild guild, float oldAmount) {
        super(guild);
        this.oldAmount = oldAmount;
    }
}
