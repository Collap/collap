package io.collap.resource;

import io.collap.Collap;
import org.hibernate.cfg.Configuration;

import java.util.List;
import java.util.logging.Logger;

public abstract class Plugin {

    private static final Logger logger = Logger.getLogger (Plugin.class.getName ());

    private boolean initialized = false;
    private List<String> dependencies = null;
    protected String name;

    protected Collap collap;

    public final void initializeWithCheck () {
        if (!initialized) {
            /* Initialize dependencies first! */
            // TODO: Check for circular dependencies.
            if (dependencies != null) {
                for (String dependencyName : dependencies) {
                    Plugin plugin = collap.getPluginManager ().getPlugins ().get (dependencyName);
                    if (plugin != null) {
                        plugin.initializeWithCheck ();
                    }else {
                        logger.warning ("Plugin '" + name + "': " + "Dependency '" + dependencyName + "' not found!");
                    }
                }
            }

            initialize ();
            initialized = true;
        }
    }

    public abstract void initialize ();
    public abstract void destroy ();

    /**
     * This method is called before the main session factory is created.
     * It is called *before* initialize.
     */
    public void configureHibernate (Configuration cfg) { }


    public final String getName () {
        return name;
    }

    public final void setName (String name) {
        this.name = name;
    }

    public final Collap getCollap () {
        return collap;
    }

    public final void setCollap (Collap collap) {
        this.collap = collap;
    }

    public void setDependencies (List<String> dependencies) {
        this.dependencies = dependencies;
    }

}
