package com.example.SearchEngine.path;

import com.example.SearchEngine.directory.FileUtilities;

import java.io.IOException;


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
        String oldFullPath = pathDirectory + "\\" + schemaID.toString();
        String newFullPath = pathDirectory + "\\" + schemaID.toString();
        return FileUtilities.deleteFile(oldFullPath)
                && FileUtilities.createFile(newFullPath, oldFullPath);
    }
}