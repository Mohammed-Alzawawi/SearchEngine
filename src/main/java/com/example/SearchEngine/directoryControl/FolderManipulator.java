package com.example.SearchEngine.directoryControl;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


public class FolderManipulator {
    String path = "C:\\Search Engine\\Directory";

    public void createFolder(JSONObject jsonObject) throws IOException {
        String folderPath = path +"\\" + jsonObject.get("id").toString();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdir();

            if (created) {
                System.out.println("Done creating the folder");
                String jsonFileName = jsonObject.get("id").toString() + "_Schema.json";
                Path jsonFilePath = Paths.get(folderPath, jsonFileName);
                Files.createFile(jsonFilePath);
                Files.write(jsonFilePath, jsonObject.toString().getBytes());
                System.out.println("Done creating the Json File");

            } else {
                System.out.println("Failed to create the folder");
            }
        } else {
            System.out.println("Folder already exists");
        }
    }

    public File getFolder(String filename) {
        File folder = new File(path +"\\" + filename);
        if (folder.exists()) {
            return folder;
        }
        return null;
    }

    public void deleteFolder(String folderName) {
        File folder = getFolder(folderName);

        if (folder != null && folder.exists()) {
            List<File> files = getSubFiles(folderName);
            for (File file : files) {
                file.delete();
            }
            boolean deleted = folder.delete();
            if (deleted) {
                System.out.println("Deleted the folder");
            }
            else {
                System.out.println("Failed to delete the folder");
            }
        }
        else {
            System.out.println("Folder does not exist");
        }
    }

    public boolean checkExistence(String filename) {
        File folder = getFolder(filename);
        return folder != null && folder.exists();
    }

    public List<File> getSubFiles(String filename) {
        File folder = getFolder(filename);

        if (folder != null && folder.isDirectory() && folder.listFiles() != null) {
            return Arrays.stream(folder.listFiles()).toList();
        }

        return null;
    }
}
