package io.collap;

import java.io.File;

public class StandardDirectories {

    public static File base;

    public static File cache;
    public static File resourceCache;

    public static File config;
    public static File pack;
    public static File plugin;

    public static void initialize () {
        base = new File ("collap");

        cache = new File (base, "cache" + File.separator);
        resourceCache = new File (cache, "resource" + File.separator);

        config = new File (base, "config" + File.separator);
        pack = new File (base, "pack" + File.separator);
        plugin = new File (base, "plugin" + File.separator);
    }

    public static void install () {
        base.mkdir ();

        cache.mkdir ();
        resourceCache.mkdir ();

        config.mkdir ();
        pack.mkdir ();
        plugin.mkdir ();
    }

}
