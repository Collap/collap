package io.collap.plugin;

import io.collap.util.FileUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * In order to use a plugin, it must first be registered. After all plugins have been registered, they are initialized.
 */
public class PluginManager {

    private static Logger logger = Logger.getLogger (PluginManager.class.toString ());

    private HashMap<String, Plugin> plugins;

    public PluginManager () {
        plugins = new HashMap<> ();
    }

    /**
     * @param fileExtension May be an empty string, as documented in {@link io.collap.util.FileUtils#crawlDirectory(java.io.File, String)} crawlDirectory}.
     */
    public void registerDirectory (File directory, String fileExtension, PluginFactory factory) {
        ArrayList<File> files = FileUtils.crawlDirectory (directory, fileExtension);
        logger.info (files.size () + " files in " + directory.getAbsolutePath ());
        for (int i = 0; i < files.size (); ++i) {
            File file = files.get (i);
            boolean success = registerFile (file, factory);
            if (!success) {
                logger.warning ("Loading plugin from file '" + file.getAbsolutePath ().substring (directory.getAbsolutePath ().length ()) + "' failed");
            }
        }
    }

    /*
     * @return Whether the file was registered as a plugin.
     */
    public boolean registerFile (File file, PluginFactory factory) {
        Plugin plugin = factory.create (file);
        if (plugin == null) {
            return false;
        }

        plugin.setOwner (this);
        plugin.setFile (file);
        register (plugin);
        return true;
    }

    public void register (Plugin plugin) {
        plugins.put (plugin.getName (), plugin);
        logger.info ("Plugin '" + plugin.getName () + "' registered");
    }

    public void findAllDependencies () {
        for (Plugin plugin : plugins.values ()) {
            plugin.findDependencies ();
        }
    }

    public void populateAllCaches () {
        for (Plugin plugin : plugins.values ()) {
            plugin.populateCacheWithCheck ();
        }
    }

    public void initializeAll () {
        for (Plugin plugin : plugins.values ()) {
            plugin.initializeWithCheck ();
        }
    }

    public void destroyAll () {
        for (Plugin plugin : plugins.values ()) {
            plugin.destroy ();
        }
    }

    public HashMap<String, Plugin> getPlugins () {
        return plugins;
    }

}
