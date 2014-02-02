package io.collap.config;

import io.collap.util.PathUtils;

import java.io.*;
import java.util.Properties;

public class Config {

    /*
     * The base directory in which all temporary files are cached and plugins are dropped.
     * In case of a relative path, make sure that you execute the servlet container in the right directory.
     */
    private String baseDirectory = "";

    /*
     * 'stream' must be an InputStream to a .properties file!
     */
    public void load (InputStream stream) throws IOException {
        Properties properties = new Properties ();
        properties.load (stream);

        /* Load paths. */
        baseDirectory = PathUtils.appendDirectorySeparator (properties.getProperty ("baseDirectory"));
    }


    public String getBaseDirectory () {
        return baseDirectory;
    }

    public void setBaseDirectory (String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

}
