package com.example.SearchEngine.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static boolean createFolder(String folderFullPath){
        File folder = new File(folderFullPath);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    public static boolean createFile(String fileFullPath, String content) throws Exception {
        Path jsonFilePath = Paths.get(fileFullPath);
        try {
            Files.createFile(jsonFilePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create the file " + fileFullPath);
        }

        try {
            Files.write(jsonFilePath, content.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not write on the file " + fileFullPath);
        }

        return Files.exists(jsonFilePath);
    }

    public static boolean deleteFolder(String folderFullPath){
        File folder = new File(folderFullPath);

        if (folder.exists()) {
            if (folder.isDirectory() && folder.listFiles() != null){
                for (File file : folder.listFiles()) {
                    if (!file.delete()){
                        return false;
                    }
                }
            }

            return folder.delete();
        }
        return false;
    }

    public static boolean deleteFile(String fileFullPath) throws Exception {
        Path file = Paths.get(fileFullPath);
        if (Files.exists(file)) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not delete the file " + fileFullPath);
            }
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean checkExistence(String fullPath) {
        File folder = new File(fullPath);
        return folder.exists();
    }
}

