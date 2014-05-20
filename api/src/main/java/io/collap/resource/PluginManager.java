package io.collap.resource;

import com.esotericsoftware.yamlbeans.YamlReader;
import io.collap.Collap;
import io.collap.StandardDirectories;
import io.collap.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * In order to use a plugin, it must first be registered. After all plugins have been registered, they are initialized.
 */
public class PluginManager {

    private static Logger logger = Logger.getLogger (PluginManager.class.toString ());

    private HashMap<String, Plugin> plugins;

    private Collap collap;

    public PluginManager (Collap collap) {
        plugins = new HashMap<> ();
        this.collap = collap;
    }

    public void registerDirectory (File directory) {
        ArrayList<File> files = FileUtils.crawlDirectory (directory, "jar");
        logger.info (files.size () + " files in " + directory.getAbsolutePath ());
        for (int i = 0; i < files.size (); ++i) {
            File file = files.get (i);
            boolean success = registerFile (file);
            if (!success) {
                logger.warning ("Loading plugin from file '" + file.getAbsolutePath ().substring (directory.getAbsolutePath ().length ()) + "' failed");
            }
        }
    }

    /*
     * @return Whether the file was registered as a plugin.
     */
    public boolean registerFile (File file) {
        /* Load the plugin config from the jar file. */
        String mainClassName = null;
        String pluginName = null;
        List<String> dependencies = null;
        JarFile pluginJar = null;
        {
            InputStream stream = null;
            try {
                pluginJar = new JarFile (file);
                ZipEntry entry = pluginJar.getEntry ("plugin.yaml");
                if (entry == null) {
                    return false;
                }
                stream = pluginJar.getInputStream (entry);

                YamlReader reader = new YamlReader (new InputStreamReader (stream));
                Map<String, Object> values = (Map<String, Object>) reader.read ();

                mainClassName = (String) values.get ("mainClass");
                pluginName = (String) values.get ("name");
                dependencies = (List<String>) values.get ("dependencies"); /* Note: May be null! */
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
        }

        /* Jar file is not a valid plugin. */
        if (mainClassName == null) return false;
        if (pluginName == null) return false;

        /* Check if plugin with the name is already registered. */
        if (plugins.containsKey (pluginName)) {
            logger.severe ("Plugin '" + pluginName + "' is already registered");
            return false;
        }

        /* Load the main class from the jar.
         * Although the classes are in the classpath we need a plugin instance. */
        URL fileUrl = null;
        try {
            fileUrl = file.toURI ().toURL ();
        }catch (MalformedURLException e) {
            e.printStackTrace ();
        }
        if (fileUrl == null) return false;

        Class mainClass = null;
        try {
            mainClass = Class.forName (mainClassName);
        }catch (ClassNotFoundException e) {
            e.printStackTrace ();
        }
        if (mainClass == null) return false;

        /* Create new instance of plugin main class. */
        Object obj = null;
        try {
            obj = mainClass.getConstructor ().newInstance ();
        } catch (InstantiationException e) {
            e.printStackTrace ();
        } catch (IllegalAccessException e) {
            e.printStackTrace ();
        } catch (InvocationTargetException e) {
            e.printStackTrace ();
        } catch (NoSuchMethodException e) {
            e.printStackTrace ();
        }
        if (obj == null) return false;

        /* Cast to Plugin. */
        Plugin plugin = (Plugin) obj;
        plugin.setName (pluginName);
        plugin.setCollap (collap);
        if (dependencies != null) plugin.setDependencies (dependencies);
        register (plugin);

        /* Populate the resource cache. */
        {
            JarInputStream stream = null;
            try {
                File pluginResourceCache = new File (StandardDirectories.resourceCache, pluginName);
                FileUtils.copyZipFilesToDirectory (file, "resource", pluginResourceCache);
            } catch (IOException e) {
                e.printStackTrace ();
            }finally {
                if (stream != null) {
                    try {
                        stream.close ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            }
        }

        return true;
    }

    public void register (Plugin plugin) {
        plugins.put (plugin.getName (), plugin);
        logger.info ("Plugin '" + plugin.getName () + "' registered");
    }

    public void initializeAllPlugins () {
        for (Plugin plugin : plugins.values ()) {
            plugin.initializeWithCheck ();
        }
    }

    public HashMap<String, Plugin> getPlugins () {
        return plugins;
    }

}
