package tv.quaint.configs;

import net.streamline.api.configs.DatabaseConfig;
import net.streamline.api.configs.ModularizedConfig;
import net.streamline.api.configs.StorageUtils;
import net.streamline.api.modules.BundledModule;
import tv.quaint.configs.roles.ConfiguredDefaultRole;
import tv.quaint.savable.SavableGroup;

import java.util.ArrayList;
import java.util.List;

public class DefaultRoles extends ModularizedConfig {
    public DefaultRoles(BundledModule module) {
        super(module, "default-roles.yml", true);
    }

    public List<ConfiguredDefaultRole> getDefaultRolesOf(Class<? extends SavableGroup> clazz) {
        List<ConfiguredDefaultRole> r = new ArrayList<>();

        for (String key : resource.singleLayerKeySet(clazz.getSimpleName())) {
            int priority = resource.getInt(clazz + "." + key + ".priority");
            String name = resource.getString(clazz + "." + key + ".name");
            int max = resource.getInt(clazz + "." + key + ".max");
            List<String> flags = resource.getStringList(clazz + "." + key + ".max");
            r.add(new ConfiguredDefaultRole(key, priority, name, max, flags));
        }

        return r;
    }
}
