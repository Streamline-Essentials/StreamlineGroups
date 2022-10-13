package tv.quaint.savable;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import tv.quaint.savable.flags.GroupFlag;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListSet;

public record SavableGroupRole(@Getter @Setter String identifier, @Getter @Setter int priority, @Getter @Setter String name,
                               @Getter @Setter int max, @Getter @Setter ConcurrentSkipListSet<String> flags) implements Comparable<SavableGroupRole> {
    public void addFlag(GroupFlag flag) {
        getFlags().add(flag.toString().toLowerCase(Locale.ROOT));
    }

    public void removeFlag(GroupFlag flag) {
        getFlags().remove(flag.toString().toLowerCase(Locale.ROOT));
    }

    public boolean hasFlag(GroupFlag flag) {
        return getFlags().contains(flag.toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public int compareTo(@NotNull SavableGroupRole o) {
        return Integer.compare(priority(), o.priority());
    }
}
