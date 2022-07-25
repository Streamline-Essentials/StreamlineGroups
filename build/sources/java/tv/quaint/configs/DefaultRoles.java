package tv.quaint.configs;

import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import tv.quaint.StreamlineGroups;
import tv.quaint.configs.roles.ConfiguredDefaultRole;
import tv.quaint.savable.SavableGroup;

import java.util.ArrayList;
import java.util.List;

public class DefaultRoles extends ModularizedConfig {
    public DefaultRoles() {
        super(StreamlineGroups.getInstance(), "default-roles.yml", true);
    }

    public List<ConfiguredDefaultRole> getDefaultRolesOf(Class<? extends SavableGroup> clazz) {
        List<ConfiguredDefaultRole> r = new ArrayList<>();

        String c = clazz.getSimpleName();
        for (String key : resource.singleLayerKeySet(c)) {
            int priority = resource.getInt(c + "." + key + ".priority");
            String name = resource.getString(c + "." + key + ".name");
            int max = resource.getInt(c + "." + key + ".max");
            List<String> flags = resource.getStringList(c + "." + key + ".flags");
            r.add(new ConfiguredDefaultRole(key, priority, name, max, flags));
        }

        return r;
    }
}
