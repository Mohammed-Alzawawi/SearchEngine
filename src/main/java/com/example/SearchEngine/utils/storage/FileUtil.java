package com.example.SearchEngine.utils.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class  FileUtil {
    public static void createFolder(String folderFullPath) {
        File folder = new File(folderFullPath);
        if (folder.exists()) {
            throw new IllegalStateException("The Folder is Already Exists");
        }
        if (!folder.mkdir()) {
            throw new IllegalStateException("Something Went Wrong While Creating The Folder");
        }
    }

    public static void createFile(String fileFullPath, String content) throws Exception {
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
    }

    public static void deleteFolder(String folderFullPath) {
        File folder = new File(folderFullPath);

        if (folder.exists()) {
            if (folder.isDirectory() && folder.listFiles() != null) {
                for (File file : folder.listFiles()) {
                    if (!file.delete()) {
                        throw new IllegalStateException("Something Went Wrong While Deleting The File");
                    }
                }
            }

            if (!folder.delete()) {
                throw new IllegalStateException("Something Went Wrong While Deleting The Folder");
            }
        }
    }

    public static void deleteFile(String fileFullPath) throws Exception {
        Path file = Paths.get(fileFullPath);
        if (!Files.exists(file)) {
            throw new IllegalStateException("No Such File: " + fileFullPath);
        }
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not delete the file " + fileFullPath);
        }
    }

    public static boolean checkExistence(String fullPath) {
        File folder = new File(fullPath);
        return folder.exists();
    }

    public static String readFileContents(String fullPath) throws IOException {
        Path fileName = Path.of(fullPath);
        return Files.readString(fileName);
    }

    public static List<String> getFilesInDirectory(String path) {
        List<String> filesPaths = new ArrayList<>();
        File directory = new File(path);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                filesPaths.add(file.getName());
            }
        }
        return filesPaths;
    }
}

