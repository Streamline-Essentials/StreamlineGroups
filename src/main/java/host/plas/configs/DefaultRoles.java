package host.plas.configs;

import net.streamline.api.configs.ModularizedConfig;
import host.plas.StreamlineGroups;
import host.plas.configs.roles.ConfiguredDefaultRole;
import host.plas.savable.SavableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class DefaultRoles extends ModularizedConfig {
    public DefaultRoles() {
        super(StreamlineGroups.getInstance(), "default-roles.yml", true);
    }

    @Override
    public void init() {

    }

    public List<ConfiguredDefaultRole> getDefaultRolesOf(Class<? extends SavableGroup> clazz) {
        List<ConfiguredDefaultRole> r = new ArrayList<>();

        String c = clazz.getSimpleName();
        for (String key : getResource().singleLayerKeySet(c)) {
            int priority = getResource().getInt(c + "." + key + ".priority");
            String name = getResource().getString(c + "." + key + ".name");
            int max = getResource().getInt(c + "." + key + ".max");
            ConcurrentSkipListSet<String> flags = new ConcurrentSkipListSet<>(getResource().getStringList(c + "." + key + ".flags"));
            r.add(new ConfiguredDefaultRole(key, priority, name, max, flags));
        }

        return r;
    }
}
