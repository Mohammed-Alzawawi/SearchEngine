package com.example.SearchEngine.directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtilities {
    public static boolean createFolder(String folderFullPath){
        File folder = new File(folderFullPath);
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return false;
    }

    public static boolean createFile(String fileFullPath, String content) throws IOException {
        Path jsonFilePath = Paths.get(fileFullPath);
        Files.createFile(jsonFilePath);
        Files.write(jsonFilePath, content.getBytes());
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

    public static boolean deleteFile(String fileFullPath) throws IOException {
        Path file = Paths.get(fileFullPath);
        if (Files.exists(file)) {
            Files.delete(file);
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

