package com.example.SearchEngine.schamePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PathController {
    String pathDirectory = "C:\\Search Engine\\Externals\\Path directory";

    public boolean createPath(String schemaID, String path) throws IOException {
        Path schemaPath = Paths.get(pathDirectory, schemaID);
        Files.createFile(schemaPath);
        Files.write(schemaPath, path.getBytes());
        return Files.exists(schemaPath);
    }

    public boolean deletePath(String schemaID) throws IOException {
        Path schemaPath = Paths.get(pathDirectory, schemaID);
        if (Files.exists(schemaPath)) {
            Files.delete(schemaPath);
            return true;
        }
        else{
            return false;
        }
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