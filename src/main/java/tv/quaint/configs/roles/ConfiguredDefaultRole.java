package tv.quaint.configs.roles;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public record ConfiguredDefaultRole(@Getter String identifier, @Getter int priority, @Getter String name,
                                    @Getter int max, @Getter ConcurrentSkipListSet<String> flags) {
}
