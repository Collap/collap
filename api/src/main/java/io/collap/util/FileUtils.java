package io.collap.util;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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

        boolean anyExtension = expectedFileExtension.isEmpty ();
        final File[] directoryFiles = directory.listFiles ();
        for (int i = 0; i < directoryFiles.length; ++i) {
            File file = directoryFiles[i];
            if (file.isDirectory ()) {
                crawlDirectory (file, expectedFileExtension, files);
            }else {
                String extension = FileUtils.getFileExtension (file.getName ());
                if (anyExtension || extension.equals (expectedFileExtension)) {
                    files.add (file);
                }
            }
        }
    }

    /**
     *  Overwrites existing files.
     */
    public static void copyZipFilesToDirectory (File file, String sourcePrefix, File target) throws IOException {
        copyZipFilesToDirectory (file, sourcePrefix, target, true);
    }

    /**
     * Copies all files from a zip file 'file' with a specific path prefix 'sourcePrefix' to a directory 'target'.
     */
    public static void copyZipFilesToDirectory (File file, String sourcePrefix, File target, boolean overwrite) throws IOException {
        ZipInputStream zipInput = new ZipInputStream (new FileInputStream (file));
        ZipFile zip = new ZipFile (file);
        ZipEntry entry;
        while ((entry = zipInput.getNextEntry ()) != null) {
            String name = entry.getName ();
            if (name.startsWith (sourcePrefix) && !entry.isDirectory ()) {
                File destination = new File (target, name.substring (sourcePrefix.length ()));
                destination.getParentFile ().mkdirs ();
                boolean exists = destination.exists ();
                if (!exists) {
                    destination.createNewFile ();
                }

                if (!exists || overwrite) {
                    OutputStream out = new FileOutputStream (destination);
                    InputStream in = zip.getInputStream (entry);

                    copy (in, out);
                }
            }
            zipInput.closeEntry ();
        }
        zipInput.close ();
    }

    /**
     * Writes all contents of an input stream to an output stream.
     * Closes 'in' and 'out'.
     */
    public static void copy (InputStream in, OutputStream out) throws IOException {
        // TODO: Copy in data chunks to allow bigger files / reduce memory usage / reduce pressure on the GC.
        byte[] data = new byte[in.available ()];
        in.read (data);
        out.write (data);
        in.close ();
        out.close ();
    }

}
