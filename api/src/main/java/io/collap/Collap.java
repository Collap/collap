package io.collap;

import io.collap.controller.Dispatcher;
import io.collap.entity.User;
import io.collap.resource.PluginManager;
import io.collap.util.FileUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Collap {

    // TODO: This singleton approach might hurt the scalability and testability of the system.

    private static Collap instance = new Collap ();

    public static Collap getInstance () {
        return instance;
    }

    private static final Logger logger = Logger.getLogger (Collap.class.getName ());

    private Config config;
    private TemplateEngine templateEngine;
    private SessionFactory sessionFactory;
    private PluginManager pluginManager;
    private Dispatcher rootDispatcher;

    protected Collap () {
        config = new Config ();
    }

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

        readConfigFiles ();

        rootDispatcher = new Dispatcher ();
        initializeTemplateEngine ();
        initializeSessionFactory ();

        /* Register and initialize plugins. */
        pluginManager = new PluginManager ();
        pluginManager.registerDirectory (StandardDirectories.plugin);
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

    /*
     * Loads the 'config/collap.properties' file. Make sure that the file is located in the same directory as
     *   the one where the servlet container is executed. If the file can not be found, the 'default.properties'
     *   file that is located in the root of the .war is loaded instead.
     */
    private void readConfigFiles () {
        // TODO: Adapt to the new configuration standards.
        /*
        try {
            InputStream defaultStream = this.getClass ().getClassLoader ().getResourceAsStream ("default.properties");
            InputStream customStream = null;
            File customConfig = new File (StandardDirectories.config, "collap.properties");
            if (customConfig.exists ()) {
                customStream = new FileInputStream (customConfig);
            }else {
                logger.info ("Custom Config does not exist. Using default configuration!");
            }

            config.load (defaultStream, customStream);

            customStream.close ();
            defaultStream.close ();
        }catch (IOException ex) {
            ex.printStackTrace ();
        }
        */
    }

    private void initializeTemplateEngine () {
        FileTemplateResolver templateResolver = new FileTemplateResolver ();
        templateResolver.setTemplateMode ("HTML5");
        templateResolver.setPrefix (StandardDirectories.resourceCache.getPath () + File.separator);
        templateResolver.setSuffix (".html");
        templateResolver.setCacheTTLMs (0L);
        templateResolver.setCacheable (false);

        templateEngine = new TemplateEngine ();
        templateEngine.setTemplateResolver (templateResolver);
    }

    private void initializeSessionFactory () {
        try {
            File configFile = new File (StandardDirectories.config, "hibernate.properties");
            Properties properties = new Properties ();
            properties.load (new FileInputStream (configFile));

            Configuration cfg = new Configuration ();
            cfg.addProperties (properties);

            cfg.addAnnotatedClass (User.class);

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

    public Config getConfig () {
        return config;
    }

    public TemplateEngine getTemplateEngine () {
        return templateEngine;
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
