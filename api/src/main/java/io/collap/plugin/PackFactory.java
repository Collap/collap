package io.collap.plugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PackFactory extends PluginFactory {

    private static final Logger logger = Logger.getLogger (PackFactory.class.getName ());

    public PackFactory (PluginManager pluginManager) {
        super (pluginManager);
    }

    @Override
    public Plugin create (File file) {
        Map<String, Object> configuration = loadConfiguration (file, "pack.yaml");
        if (configuration == null) {
            logger.severe ("Configuration not found! (File: " + file.getName () + ")");
            return null;
        }

        String packName = (String) configuration.get ("name");
        List<String> dependencyNames = (List<String>) configuration.get ("dependencies"); /* Note: May be null! */

        /* Check if all mandatory config options are set. */
        if (packName == null) return null;

        /* Check if plugin with the name is already registered. */
        if (isPluginAlreadyRegistered (packName)) {
            logger.severe ("Plugin '" + packName + "' is already registered");
            return null;
        }

        Pack pack = new Pack ();
        pack.setName (packName);
        if (dependencyNames != null) pack.setDependencyNames (dependencyNames);
        return pack;
    }

}
