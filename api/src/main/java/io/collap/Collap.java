package io.collap;

import io.collap.config.Config;
import io.collap.entity.User;
import io.collap.plugin.PluginManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Collap {

    private static Collap instance = new Collap ();

    public static Collap getInstance () {
        return instance;
    }

    private static final Logger logger = Logger.getLogger (Collap.class.getName ());

    private Config config;
    private TemplateEngine templateEngine;
    private SessionFactory sessionFactory;
    private PluginManager pluginManager;

    protected Collap () {
        config = new Config ();
    }

    /*
     * Loads the 'collap.properties' file. Make sure that the file is located in the same directory as
     *   the one where the servlet container is executed. If the file can not be found, the 'default.properties'
     *   file that is located in the root of the .war is loaded instead.
     */
    public void initialize () {
        readPropertyFile ();
        StandardDirectories.initialize ();

        /* Initialize plugins. */
        pluginManager = new PluginManager ();
        pluginManager.registerDirectory (StandardDirectories.plugin);

        initializeTemplateEngine ();
        initializeSessionFactory ();
    }

    private void readPropertyFile () {
        try {
            InputStream defaultStream = this.getClass ().getClassLoader ().getResourceAsStream ("default.properties");
            InputStream customStream = null;
            File customConfig = new File ("collap.properties");
            if (customConfig.exists ()) {
                customStream = new FileInputStream (customConfig);
            }else {
                logger.severe ("Custom Config does not exist!"
                        + "\nSearched for: " + customConfig.getAbsolutePath ());
            }

            config.load (defaultStream, customStream);

            customStream.close ();
            defaultStream.close ();
        }catch (IOException ex) {
            ex.printStackTrace ();
        }
    }

    private void initializeTemplateEngine () {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver ();
        templateResolver.setTemplateMode ("HTML5");
        templateResolver.setPrefix ("template/");
        templateResolver.setSuffix (".html");
        templateResolver.setCacheTTLMs (0L);
        templateResolver.setCacheable (false);

        templateEngine = new TemplateEngine ();
        templateEngine.setTemplateResolver (templateResolver);
    }

    private void initializeSessionFactory () {
        Configuration cfg = new Configuration ();
        cfg.setProperty (AvailableSettings.DRIVER, "com.mysql.jdbc.Driver"); // TODO: Make Collap MySQL independent
        cfg.setProperty (AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
        cfg.setProperty (AvailableSettings.HBM2DDL_AUTO, "update"); /* Create tables that do not exist automatically and update existing ones. */
        cfg.setProperty (AvailableSettings.URL, config.getDatabaseConnectionUrl ());
        cfg.setProperty (AvailableSettings.USER, config.getDatabaseUserName ());
        cfg.setProperty (AvailableSettings.PASS, config.getDatabaseUserPassword ());

        cfg.addAnnotatedClass (User.class);

        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder ();
        ssrb.applySettings (cfg.getProperties ());
        StandardServiceRegistry registry = ssrb.build ();
        sessionFactory = cfg.buildSessionFactory (registry);
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

}
