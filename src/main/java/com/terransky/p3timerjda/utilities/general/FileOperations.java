package com.terransky.p3timerjda.utilities.general;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileOperations {

    /**
     * Attempts to make a directory path.
     *
     * @param pathName The path of the directory
     * @return True if the operation was successful
     */
    public static boolean makeDirectory(@NotNull String pathName) {
        File file = new File(pathName);
        if (!file.isDirectory()) return false;
        if (file.exists())
            return true;
        return file.mkdir();
    }
}
