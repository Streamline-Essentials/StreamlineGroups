package host.plas.savable.guilds;

import lombok.Getter;

public class LevelChangeGuildEvent extends ExperienceSavableGuildEvent {
    @Getter
    private final int oldLevel;

    public LevelChangeGuildEvent(SavableGuild guild, int oldLevel) {
        super(guild);
        this.oldLevel = oldLevel;
    }
}
