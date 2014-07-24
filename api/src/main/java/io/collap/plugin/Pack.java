package io.collap.plugin;

import io.collap.StandardDirectories;
import io.collap.util.FileUtils;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.IOException;

public class Pack extends Plugin {

    /**
     * A pack replaces the resources of another plugin with its own resources.
     * Therefore, this method has to be called after the resources of plugins it overrides have been copied to the cache.
     * This should be ensured by the plugin manager.
     */
    @Override
    public void populateCache () {
        for (Plugin plugin : dependencies) {
            try {
                File resourceCache = new File (StandardDirectories.resourceCache, plugin.getName ());
                FileUtils.copyZipFilesToDirectory (file, plugin.getName (), resourceCache);
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

    @Override
    public void configureHibernate (Configuration configuration) {

    }

    @Override
    public void initialize () {

    }

    @Override
    public void destroy () {

    }

}
