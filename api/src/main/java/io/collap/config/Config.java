package io.collap.config;

import io.collap.util.FileUtils;

import java.io.*;
import java.util.Properties;

public class Config {

    /*
     * The base directory in which all temporary files are cached and plugins are dropped.
     * In case of a relative path, make sure that you execute the servlet container in the right directory.
     */
    private String baseDirectory = "";

    private String databaseConnectionUrl = "";
    private String databaseUserName = "";
    private String databaseUserPassword = "";

    /*
     * 'customStream' and 'defaultStream' must be InputStreams to a .properties file!
     * 'customStream' may be null.
     * 'defaultStream' reflects the default configuration file.
     * Settings in 'customStream' override settings in 'defaultStream'.
     */
    public void load (InputStream defaultStream, InputStream customStream) throws IOException {
        Properties properties = new Properties ();
        properties.load (defaultStream);
        if (customStream != null) {
            properties.load (customStream);
        }

        /* Application. */
        baseDirectory = FileUtils.appendDirectorySeparator (properties.getProperty ("baseDirectory"));

        /* Database. */
        databaseConnectionUrl = properties.getProperty ("databaseConnectionUrl");
        databaseUserName = properties.getProperty ("databaseUserName");
        databaseUserPassword = properties.getProperty ("databaseUserPassword");
    }

    public String getBaseDirectory () {
        return baseDirectory;
    }

    public void setBaseDirectory (String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getDatabaseConnectionUrl () {
        return databaseConnectionUrl;
    }

    public void setDatabaseConnectionUrl (String databaseConnectionUrl) {
        this.databaseConnectionUrl = databaseConnectionUrl;
    }

    public String getDatabaseUserName () {
        return databaseUserName;
    }

    public void setDatabaseUserName (String databaseUserName) {
        this.databaseUserName = databaseUserName;
    }

    public String getDatabaseUserPassword () {
        return databaseUserPassword;
    }

    public void setDatabaseUserPassword (String databaseUserPassword) {
        this.databaseUserPassword = databaseUserPassword;
    }

}