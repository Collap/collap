package io.collap;

import io.collap.controller.Dispatcher;
import io.collap.entity.User;
import io.collap.resource.PluginManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        readPropertyFile ();
        StandardDirectories.initialize ();

        rootDispatcher = new Dispatcher ();
        initializeTemplateEngine ();
        initializeSessionFactory ();

        /* Register and initialize plugins. */
        pluginManager = new PluginManager ();
        pluginManager.registerDirectory (StandardDirectories.plugin);
        pluginManager.initializeAllPlugins ();
    }

    /*
     * Loads the 'collap.properties' file. Make sure that the file is located in the same directory as
     *   the one where the servlet container is executed. If the file can not be found, the 'default.properties'
     *   file that is located in the root of the .war is loaded instead.
     */
    private void readPropertyFile () {
        try {
            InputStream defaultStream = this.getClass ().getClassLoader ().getResourceAsStream ("default.properties");
            InputStream customStream = null;
            File customConfig = new File ("collap.properties");
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
            Configuration cfg = new Configuration ();
            cfg.setProperty (AvailableSettings.DRIVER, "com.mysql.jdbc.Driver");
            cfg.setProperty (AvailableSettings.DATASOURCE, "java:comp/env/jdbc/collap");
            cfg.setProperty (AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
            cfg.setProperty (AvailableSettings.HBM2DDL_AUTO, "update"); /* Create tables that do not exist automatically and update existing ones. */

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
