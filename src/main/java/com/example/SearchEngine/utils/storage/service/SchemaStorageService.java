package com.example.SearchEngine.utils.storage.service;

import com.example.SearchEngine.constants.Constants;
import com.example.SearchEngine.utils.storage.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SchemaStorageService {
    @Autowired
    private SchemaPathService schemaPathService;
    @Autowired
    private ObjectMapper mapper;

    private void checkID(JsonNode jsonNode){
        if (!jsonNode.has("id") || (!jsonNode.get("id").isInt() && !jsonNode.get("id").isLong())) {
            throw new IllegalStateException("ID not found");
        }
    }

    private void createFolder(JsonNode jsonNode) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id").toString();
        try {
            FileUtil.createFolder(folderPath);
            FileUtil.createFolder(folderPath  + "/documents");
            System.out.println("Done creating the folder");
        }
        catch (Exception e) {
            throw new IllegalStateException("Problem with creating the Folder!");
        }
    }

    private void createJsonFile(JsonNode jsonNode) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id").toString();
        String jsonFilePath = folderPath + "/" + jsonNode.get("id").toString() + "_Schema.json";
        String content = null;
        try {
            content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error writing json file");
        }
        try {
            FileUtil.createFile(jsonFilePath, content);
            System.out.println("Done creating the Json File");
        }
        catch (Exception e) {
            throw new IllegalStateException("Problem with creating the Json File!");
        }
    }

    private void createLogFiles(JsonNode jsonNode) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id").toString();
        try {
            FileUtil.createFile(folderPath + "/" + "log.txt", "");
            FileUtil.createFile(folderPath + "/" + "currentLog.txt", "");
            System.out.println("Done creating the Log files");
        }
        catch (Exception e) {
            throw new IllegalStateException("Problem with creating the Log files");
        }
    }

    private void addPathFile(JsonNode jsonNode) throws Exception {
        try {
            schemaPathService.createPath(jsonNode.get("id").toString(), Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id").toString() + '/' );
            System.out.println("Done creating the path file");
        }
        catch (Exception e) {
            throw new IllegalStateException("Problem with creating the path file!");
        }
    }

    private void deleteFolder(JsonNode jsonNode) throws Exception {
        String folderPath = Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id").toString();
        try {
            FileUtil.deleteFolder(folderPath);
        }
        catch (Exception e) {
            System.out.println("Problem with deleting the folder!");
        }
        try {
            schemaPathService.deletePath(jsonNode.get("id").toString());
            System.out.println("Done deleting the folder");
        }
        catch (Exception e) {
            System.out.println("Problem with deleting the path file!");
        }
    }

    public void saveSchemaFile(JsonNode jsonNode) throws Exception {
        checkID(jsonNode);
        String identifier = jsonNode.get("id").toString();
        if (FileUtil.checkExistence(Constants.Paths.SCHEMA_STORAGE_PATH + identifier)){
            throw new IllegalStateException("Folder " + identifier + " already exists");
        }
        else {
            createFolder(jsonNode);
            createJsonFile(jsonNode);
            createLogFiles(jsonNode);
            FileUtil.createFile(Constants.Paths.SCHEMA_STORAGE_PATH + jsonNode.get("id") + "/trie", "");
            addPathFile(jsonNode);
            System.out.println("- Adding Json to directory is done.");
        }
    }
    public void deleteSchemaFile(JsonNode jsonNode) throws Exception {
        checkID(jsonNode);
        deleteFolder(jsonNode);
    }
    public void updateSchemaFile(JsonNode jsonNode, String key, Object value) throws Exception {
        checkID(jsonNode);
        String identifier = jsonNode.get("id").toString();
        if (!FileUtil.checkExistence(Constants.Paths.SCHEMA_STORAGE_PATH + identifier)){
            throw new IllegalStateException("This Json file does not exist in the directory");
        }
        if (!jsonNode.has(key)) {
            throw new IllegalStateException("This key does not exist");
        }
        if (Objects.equals(key, "id")){
            throw new IllegalStateException("Can't modify the Id of the Json");
        }
        JsonNode node = mapper.valueToTree(value);
        ObjectNode objectNode = (ObjectNode) jsonNode;
        objectNode.put(key, node);
        deleteSchemaFile(jsonNode);
        createFolder(jsonNode);
        System.out.println("- Updated the " + key + " to " + value);
    }
}
