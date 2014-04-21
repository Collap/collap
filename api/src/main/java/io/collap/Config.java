package io.collap;

import java.io.*;
import java.util.Properties;

// TODO: This class is currently obsolete.
public class Config {

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
    }

}