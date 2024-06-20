package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.utils.FileUtil;
import org.springframework.stereotype.Service;

@Service
public class SchemaPathService {
    String pathDirectory = "C:\\Search Engine\\Externals\\Path directory";

    public boolean createPath(String schemaID, String path) throws Exception {
        String fullPath = pathDirectory + "\\" + schemaID;
        return FileUtil.createFile(fullPath, path);
    }

    public boolean deletePath(String schemaID) throws Exception {
        String fullPath = pathDirectory + "\\" + schemaID;
        return FileUtil.deleteFile(fullPath);
    }

    public boolean updatePath(Long schemaID, String newPathDirectory) throws Exception {
        String oldFullPath = pathDirectory + "\\" + schemaID.toString();
        String newFullPath = pathDirectory + "\\" + schemaID.toString();
        return FileUtil.deleteFile(oldFullPath)
                && FileUtil.createFile(newFullPath, newPathDirectory);
    }
}