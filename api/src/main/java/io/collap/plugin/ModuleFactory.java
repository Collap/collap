package io.collap.plugin;

import io.collap.Collap;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ModuleFactory extends PluginFactory {

    private static final Logger logger = Logger.getLogger (ModuleFactory.class.getName ());

    private Collap collap;

    public ModuleFactory (PluginManager pluginManager, Collap collap) {
        super (pluginManager);
        this.collap = collap;
    }

    @Override
    public Plugin create (File file) {
        Map<String, Object> configuration = loadConfiguration (file, "module.yaml");
        if (configuration == null) {
            logger.severe ("Configuration not found! (File: " + file.getName () + ")");
            return null;
        }

        String moduleName = (String) configuration.get ("name");
        List<String> dependencyNames = (List<String>) configuration.get ("dependencies"); /* Note: May be null! */
        String mainClassName = (String) configuration.get ("mainClass");

        /* Check if all mandatory config options are set. */
        if (mainClassName == null) return null;
        if (moduleName == null) return null;

        /* Check if plugin with the name is already registered. */
        if (isPluginAlreadyRegistered (moduleName)) {
            logger.severe ("Plugin '" + moduleName + "' is already registered");
            return null;
        }

        /* Load the main class from the jar.
         * Although the classes are in the classpath we need a module instance. */
        URL fileUrl = null;
        try {
            fileUrl = file.toURI ().toURL ();
        }catch (MalformedURLException e) {
            e.printStackTrace ();
        }
        if (fileUrl == null) return null;

        Class mainClass = null;
        try {
            mainClass = Class.forName (mainClassName);
        }catch (ClassNotFoundException e) {
            e.printStackTrace ();
        }
        if (mainClass == null) return null;

        /* Create new instance of module main class. */
        Object obj = null;
        try {
            obj = mainClass.getConstructor ().newInstance ();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace ();
        }
        if (obj == null) return null;

        /* Cast to Module. */
        Module module = (Module) obj;
        module.setName (moduleName);
        module.setCollap (collap);
        if (dependencyNames != null) module.setDependencyNames (dependencyNames);

        return module;
    }

}
