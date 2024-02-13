package host.plas.savable;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import host.plas.savable.flags.GroupFlag;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentSkipListSet;

public class SavableGroupRole implements Comparable<SavableGroupRole> {
    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private int priority;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private int max;
    @Getter @Setter
    private ConcurrentSkipListSet<String> flags;

    public SavableGroupRole(String identifier, int priority, String name, int max, ConcurrentSkipListSet<String> flags) {
        setIdentifier(identifier);
        setPriority(priority);
        setName(name);
        setMax(max);
        setFlags(flags);
    }

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
        return Integer.compare(getPriority(), o.getPriority());
    }
}
