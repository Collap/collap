package io.collap.util;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils {

    private static final Logger logger = Logger.getLogger (FileUtils.class.getName ());

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

    /**
     *  Copies all files from a zip file 'file' with a specific path prefix 'sourcePrefix' to a directory 'target'.
     *  Overwrites existing files.
     */
    public static void copyZipFilesToDirectory (File file, String sourcePrefix, File target) throws IOException {
        ZipInputStream zipInput = new ZipInputStream (new FileInputStream (file));
        ZipFile zip = new ZipFile (file);
        ZipEntry entry;
        while ((entry = zipInput.getNextEntry ()) != null) {
            String name = entry.getName ();
            if (name.startsWith (sourcePrefix) && !entry.isDirectory ()) {
                File destination = new File (target, name.substring (sourcePrefix.length ()));
                destination.getParentFile ().mkdirs ();
                if (!destination.exists ()) {
                    destination.createNewFile ();
                }

                OutputStream out = new FileOutputStream (destination);
                InputStream in = zip.getInputStream (entry);

                // TODO: Copy in data chunks to allow bigger files / reduce memory usage / reduce pressure on the GC.
                byte[] data = new byte[(int) entry.getSize ()];
                in.read (data);
                out.write (data);
                in.close ();
                out.close ();
            }
            zipInput.closeEntry ();
        }
        zipInput.close ();
    }

}
