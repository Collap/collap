package io.collap.resource;

import io.collap.StandardDirectories;
import io.collap.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
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

    public PluginManager () {
        plugins = new HashMap<> ();
    }

    public void registerDirectory (File directory) {
        ArrayList<File> files = FileUtils.crawlDirectory (directory, "jar");
        logger.severe (files.size () + " files in " + directory.getAbsolutePath ());
        for (int i = 0; i < files.size (); ++i) {
            File file = files.get (i);
            boolean success = registerFile (file);
            if (!success) {
                logger.severe ("Loading plugin from file '" + file.getAbsolutePath ().substring (directory.getAbsolutePath ().length ()) + "' failed");
            }
        }
    }

    /*
     * @return Whether the file was registered as a plugin.
     */
    public boolean registerFile (File file) {
        /* Check if plugin with the name is already registered. */
        String fileName = file.getName ();
        int extensionStart = fileName.lastIndexOf ('.');
        String pluginName = fileName.substring (0, extensionStart);
        if (plugins.containsKey (pluginName)) {
            logger.severe ("Plugin '" + pluginName + "' is already registered");
            return false;
        }

        /* Load the plugin config from the jar file. */
        String mainClassName = null;
        JarFile pluginJar = null;
        {
            InputStream stream = null;
            try {
                pluginJar = new JarFile (file);
                ZipEntry entry = pluginJar.getEntry ("plugin.properties");
                if (entry == null) {
                    return false;
                }
                stream = pluginJar.getInputStream (entry);
                Properties properties = new Properties ();
                properties.load (stream);
                mainClassName = properties.getProperty ("mainClass");
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

        /* Load the main class from the jar. This will also load other classes on demand. */
        URL fileUrl = null;
        try {
            fileUrl = file.toURI ().toURL ();
        }catch (MalformedURLException e) {
            e.printStackTrace ();
        }
        if (fileUrl == null) return false;

        URLClassLoader child = new URLClassLoader (new URL[] { fileUrl }, this.getClass ().getClassLoader ());
        Class mainClass = null;
        try {
            mainClass = Class.forName (mainClassName, true, child);
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
        // TODO: Clarify order of initialization and provide a method to affect this order (implicit or explicit).
        for (Plugin plugin : plugins.values ()) {
            plugin.initializeWithCheck ();
        }
    }

}
