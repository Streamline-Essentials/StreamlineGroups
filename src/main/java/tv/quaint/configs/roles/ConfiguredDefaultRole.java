package tv.quaint.configs.roles;

import lombok.Getter;

import java.util.List;

public record ConfiguredDefaultRole(@Getter String identifier, @Getter int priority, @Getter String name,
                                    @Getter int max, @Getter List<String> flags) {
}
