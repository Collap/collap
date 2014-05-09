package io.collap;

import io.collap.controller.Dispatcher;
import io.collap.resource.Plugin;
import io.collap.resource.PluginManager;
import io.collap.util.FileUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Collap {

    private static final Logger logger = Logger.getLogger (Collap.class.getName ());

    private SessionFactory sessionFactory;
    private PluginManager pluginManager;
    private Dispatcher rootDispatcher;

    public void initialize () {
        StandardDirectories.initialize ();

        /* Check whether collap has already been installed. */
        File installFile = new File (StandardDirectories.base, "install");
        if (!installFile.exists ()) {
            // TODO: Enable install file generation in production.
            /* try {
                installFile.createNewFile ();
            } catch (IOException e) {
                logger.severe ("Could not create installation notice file 'install'.");
                e.printStackTrace ();
            } */
            install ();
        }

        /* Register plugins. */
        pluginManager = new PluginManager (this);
        pluginManager.registerDirectory (StandardDirectories.plugin);

        rootDispatcher = new Dispatcher ();
        initializeSessionFactory ();

        /* Initialize plugins. */
        pluginManager.initializeAllPlugins ();
    }

    /*
     * Sets up the collap directory.
     */
    private void install () {
        StandardDirectories.install ();

        /* Copy the 'default' directory contents from the WAR. */
        // TODO: Copy the whole directory.
        /* Only copy the hibernate config if it was not created yet. */
        File cpConfig = new File (StandardDirectories.config, "hibernate.properties");
        if (!cpConfig.exists ()) {
            try {
                FileUtils.copy (getClass ().getResourceAsStream ("/default/config/hibernate.properties"),
                        new FileOutputStream (cpConfig));
            } catch (IOException e) {
                logger.severe ("Could not copy the default hibernate.properties file!");
                e.printStackTrace ();
            }
        }
    }

    private void initializeSessionFactory () {
        try {
            File configFile = new File (StandardDirectories.config, "hibernate.properties");
            Properties properties = new Properties ();
            properties.load (new FileInputStream (configFile));

            Configuration cfg = new Configuration ();
            cfg.addProperties (properties);

            /* Let plugins configure Hibernate. */
            for (Plugin plugin : pluginManager.getPlugins ().values ()) {
                plugin.configureHibernate (cfg);
            }

            StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder ();
            ssrb.applySettings (cfg.getProperties ());
            StandardServiceRegistry registry = ssrb.build ();
            sessionFactory = cfg.buildSessionFactory (registry);
        }catch (Exception e) {
            logger.log (Level.SEVERE, "Error: ", e);
        }
    }

    public void destroy () {
        sessionFactory.close ();
    }

    public SessionFactory getSessionFactory () {
        return sessionFactory;
    }

    public PluginManager getPluginManager () {
        return pluginManager;
    }

    public Dispatcher getRootDispatcher () {
        return rootDispatcher;
    }

}
