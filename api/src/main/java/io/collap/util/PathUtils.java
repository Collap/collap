package io.collap.util;

import java.io.File;

public class PathUtils {

    public static String appendDirectorySeparator (String path) {
        char lastChar = path.charAt (path.length () - 1);
        if (lastChar != '/' && lastChar != '\\') {
            path += File.separator;
        }
        return path;
    }

}
