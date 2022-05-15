package org.ssiu.ucp.util.base;

import java.io.File;

public class FileUtils {
    /**
     * Get fileName by given paths
     *
     * @param filePath file path
     * @return file name
     */
    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
    }
}
