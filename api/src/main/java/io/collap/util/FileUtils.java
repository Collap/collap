package io.collap.util;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FileUtils {

    public static String appendDirectorySeparator (String path) {
        char lastChar = path.charAt (path.length () - 1);
        if (lastChar != '/' && lastChar != '\\') {
            path += File.separator;
        }
        return path;
    }

    public static String getFileExtension (String path) {
        int lastDot = path.lastIndexOf ('.');
        if (lastDot != -1 && lastDot + 1 < path.length ()) {
            return path.substring (lastDot + 1, path.length ());
        }
        return "";
    }

    public static ArrayList<File> crawlDirectory (File directory, String expectedFileExtension) {
        ArrayList<File> files = new ArrayList<> ();
        crawlDirectory (directory, expectedFileExtension, files);
        return files;
    }

    public static void crawlDirectory (File directory, String expectedFileExtension, ArrayList<File> files) {
        if (!directory.exists () || !directory.isDirectory ()) {
            return;
        }

        Logger logger = Logger.getLogger (FileUtils.class.toString ());

        boolean anyExtension = expectedFileExtension.isEmpty ();
        final File[] directoryFiles = directory.listFiles ();
        logger.severe ("" + directoryFiles.length);
        for (int i = 0; i < directoryFiles.length; ++i) {
            File file = directoryFiles[i];
            logger.severe (file.getAbsolutePath ());
            if (file.isDirectory ()) {
                crawlDirectory (file, expectedFileExtension, files);
            }else {
                String extension = FileUtils.getFileExtension (file.getName ());
                logger.severe (extension);
                if (anyExtension || extension.equals (expectedFileExtension)) {
                    files.add (file);
                }
            }
        }
    }

}
