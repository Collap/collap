package io.collap.plugin;

import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class Plugin {

    private static final Logger logger = Logger.getLogger (Plugin.class.getName ());

    private boolean initialized = false;
    private boolean cachePopulated = false;

    protected String name; /* Set by the plugin factory. */
    protected File file; /* Set by the plugin manager. */
    protected PluginManager owner; /* Set by the plugin manager. */

    /* Dependencies. */
    protected List<String> dependencyNames = null; /* null after findDependencies() has been called! */
    protected List<Plugin> dependencies = null;

    /**
     * This method is executed after all plugins have been registered.
     */
    // TODO: Check for circular dependencies?
    public final void findDependencies () {
        if (dependencyNames != null) {
            dependencies = new ArrayList<> ();
            for (String dependencyName : dependencyNames) {
                Plugin plugin = owner.getPlugins ().get (dependencyName);
                if (plugin != null) {
                    dependencies.add (plugin);
                }else {
                    logger.warning ("Plugin '" + name + "': " + "Dependency '" + dependencyName + "' not found!");
                }
            }
            dependencyNames = null;
        }
    }

    public final void populateCacheWithCheck () {
        if (!cachePopulated) {
            if (dependencies != null) {
                for (Plugin dependency : dependencies) {
                    dependency.populateCacheWithCheck ();
                }
            }

            populateCache ();
            cachePopulated = true;
        }
    }

    public final void initializeWithCheck () {
        if (!initialized) {
            /* Initialize dependencies first! */
            if (dependencies != null) {
                for (Plugin dependency : dependencies) {
                    dependency.initializeWithCheck ();
                }
            }

            initialize ();
            initialized = true;
        }
    }

    /* The following abstract methods are declared in the order in which they are invoked during the plugin lifecycle.*/

    /**
     * Called after the plugin has been loaded and registered.
     */
    public abstract void populateCache ();

    public abstract void configureHibernate (Configuration configuration);

    public abstract void initialize ();

    public abstract void destroy ();

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public File getFile () {
        return file;
    }

    public void setFile (File file) {
        this.file = file;
    }

    public PluginManager getOwner () {
        return owner;
    }

    public void setOwner (PluginManager owner) {
        this.owner = owner;
    }

    public void setDependencyNames (List<String> dependencyNames) {
        this.dependencyNames = dependencyNames;
    }

}
