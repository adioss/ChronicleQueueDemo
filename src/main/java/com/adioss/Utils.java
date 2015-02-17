package com.adioss;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public final static String TEST_INDEX_DIRECTORY_PATH = "d:\\test";
    public final static String TEST_BIG_FILE_PATH = "d:\\test.pdf";
    public final static String HOST = "localhost";
    public final static int PORT = 12345;

    public static void deleteIndexBeforeStart(String basePath) {
        try {
            Path path = Paths.get(basePath);
            boolean exists = Files.exists(path);
            boolean directory = Files.isDirectory(path);

            if (exists && directory) {
                File file = new File(basePath);
                delete(file);
            }
        } catch (IOException e) {
            System.out.println("Impossible to delete index path");
            System.exit(0);
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                delete(c);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + file);
        }
    }

    public static String prepareIndexDirectory(String indexDirectory) {
        Utils.deleteIndexBeforeStart(indexDirectory);
        return Utils.getIndexPath(indexDirectory);
    }

    private static String getIndexPath(String directory) {
        return Paths.get(directory + "\\file\\").toString();
    }

    public static String waitForIndexPath(String directory) {

        boolean waiting = true;
        while (waiting) {
            if (Files.exists(Paths.get(directory))) {
                waiting = false;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        }

        return getIndexPath(directory);
    }
}
