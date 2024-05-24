package com.example.SearchEngine.path;

import com.example.SearchEngine.directory.FileUtilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PathController {
    String pathDirectory = "C:\\Search Engine\\Externals\\Path directory";

    public boolean createPath(String schemaID, String path) throws IOException {
        String fullPath = pathDirectory + "\\" + schemaID;
        return FileUtilities.createFile(fullPath, path);
    }

    public boolean deletePath(String schemaID) throws IOException {
        String fullPath = pathDirectory + "\\" + schemaID;
        return FileUtilities.deleteFile(fullPath);
    }

    public boolean updatePath(Long schemaID, String newPathDirectory) throws IOException {
        Path schemaPath = Paths.get(pathDirectory, schemaID.toString());
        if (Files.exists(schemaPath)) {
            Files.delete(schemaPath);
            Path newPath = Paths.get(newPathDirectory, schemaID.toString());
            Files.createFile(newPath);
            Files.write(newPath, newPathDirectory.getBytes());
            return true;
        }
        else{
            return false;
        }
    }
}