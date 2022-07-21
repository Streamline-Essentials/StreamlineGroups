package tv.quaint.savable;

import lombok.Getter;
import lombok.Setter;
import tv.quaint.savable.flags.GroupFlag;

import java.util.List;
import java.util.Locale;

public record SavableGroupRole(@Getter @Setter String identifier, @Getter @Setter int priority, @Getter @Setter String name,
                               @Getter @Setter int max, @Getter @Setter List<String> flags) {
    public void addFlag(GroupFlag flag) {
        getFlags().add(flag.toString().toLowerCase(Locale.ROOT));
    }

    public void removeFlag(GroupFlag flag) {
        getFlags().remove(flag.toString().toLowerCase(Locale.ROOT));
    }

    public boolean hasFlag(GroupFlag flag) {
        return getFlags().contains(flag.toString().toLowerCase(Locale.ROOT));
    }
}
