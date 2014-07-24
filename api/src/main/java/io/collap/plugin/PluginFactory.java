package io.collap.plugin;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class PluginFactory {

    protected PluginManager pluginManager;

    public PluginFactory (PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * @param file The plugin that should be created.
     * @return The plugin object or null if the plugin was not found or not valid.
     */
    public abstract Plugin create (File file);

    protected Map<String, Object> loadConfiguration (File file, String configFileName) {
        Map<String, Object> configuration = null;
        InputStream stream = null;
        try {
            ZipFile packZip = new ZipFile (file);
            ZipEntry entry = packZip.getEntry (configFileName);
            if (entry == null) {
                return null;
            }
            stream = packZip.getInputStream (entry);

            YamlReader reader = new YamlReader (new InputStreamReader (stream));
            configuration = (Map<String, Object>) reader.read ();
        } catch (IOException e) {
            e.printStackTrace ();
        } finally {
            if (stream != null) {
                try {
                    stream.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }

        return configuration;
    }

    protected boolean isPluginAlreadyRegistered (String name) {
        return pluginManager.getPlugins ().containsKey (name);
    }

}
