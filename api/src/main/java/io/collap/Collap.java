package io.collap;

import io.collap.config.Config;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;

public class Collap {
    private static Collap instance = new Collap ();

    public static Collap getInstance () {
        return instance;
    }


    private Config config;

    private TemplateEngine templateEngine;




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
        initializeTemplateEngine ();
    }

    private void readPropertyFile () {
        try {
            InputStream stream;

            File customConfig = new File ("collap.properties");
            if (customConfig.exists ()) {
                stream = new FileInputStream (customConfig);
            }else {
                stream = this.getClass ().getClassLoader ().getResourceAsStream ("res/default.properties");
            }

            config.load (stream);

            stream.close ();
        }catch (IOException ex) {
            ex.printStackTrace ();
        }
    }

    private void initializeTemplateEngine () {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver ();
        templateResolver.setTemplateMode ("HTML5");
        templateResolver.setPrefix ("res/template/");
        templateResolver.setSuffix (".html");
        templateResolver.setCacheTTLMs (3600000L);
        templateResolver.setCacheable (false);

        templateEngine = new TemplateEngine ();
        templateEngine.setTemplateResolver (templateResolver);
    }

    public Config getConfig () {
        return config;
    }

    public TemplateEngine getTemplateEngine () {
        return templateEngine;
    }

}
