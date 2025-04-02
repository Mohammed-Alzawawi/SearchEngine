package com.example.SearchEngine.utils.storage.service;

import com.example.SearchEngine.constants.Constants.*;
import com.example.SearchEngine.utils.storage.FileUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SchemaPathService {

    public void createPath(String id, String path) throws Exception {
        String fullPath = Paths.SCHEMA_PATH_DICTIONARY_PATH + id;
        FileUtil.createFile(fullPath, path);
    }

    public void deletePath(String schemaID) throws Exception {
        String fullPath = Paths.SCHEMA_PATH_DICTIONARY_PATH + schemaID;
        FileUtil.deleteFile(fullPath);
    }

    public void updatePath(String id, String newPathDirectory) throws Exception {
        String oldFullPath = Paths.SCHEMA_PATH_DICTIONARY_PATH + id;
        String newFullPath = Paths.SCHEMA_PATH_DICTIONARY_PATH + id;
        FileUtil.deleteFile(oldFullPath);
        FileUtil.createFile(newFullPath, newPathDirectory);
    }

    public String getSchemaPath(String schemaID) throws IOException {
        String fullPath = Paths.SCHEMA_PATH_DICTIONARY_PATH + schemaID;
        return FileUtil.readFileContents(fullPath);
    }
}