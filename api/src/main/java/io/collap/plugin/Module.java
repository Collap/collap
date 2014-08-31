package io.collap.plugin;

import io.collap.Collap;
import io.collap.StandardDirectories;
import io.collap.controller.provider.ModuleProvider;
import io.collap.util.FileUtils;

import java.io.File;
import java.io.IOException;

public abstract class Module extends Plugin implements ModuleProvider {

    protected Collap collap;

    @Override
    public void populateCache () {
        try {
            File resourceCache = new File (StandardDirectories.resourceCache, name);
            FileUtils.copyZipFilesToDirectory (file, "resource", resourceCache);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public Collap getCollap () {
        return collap;
    }

    public void setCollap (Collap collap) {
        this.collap = collap;
    }

    @Override
    public Module getModule () {
        return this;
    }

}
