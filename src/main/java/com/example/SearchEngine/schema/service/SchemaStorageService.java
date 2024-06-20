package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.utils.FileUtil;
import com.example.SearchEngine.utils.JsonParserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SchemaStorageService {
    @Autowired
    private SchemaFolderService folderController;
    @Autowired
    private ObjectMapper mapper;

    public void start() throws Exception {
        String filePath = "C:\\Search Engine\\Services\\centuryBoys.json";
        JsonNode jsonNode = JsonParserUtil.jsonFileToJsonNode(filePath);
        System.out.println(jsonNode);
//        addJson(jsonNode);
//        updateJson(jsonNode, "name", "Century Boys {Updated}");
//        deleteJson(jsonNode);
    }


    public boolean checkID(JsonNode jsonNode){
        return jsonNode.has("id") && (jsonNode.get("id").isInt() || jsonNode.get("id").isLong());
    }

    public void deleteJson(JsonNode jsonNode) throws Exception {
        if (checkID(jsonNode)) {
            // check folderController
            folderController.deleteFolder(jsonNode);
        }
        else{
            System.out.println("ID not found");
        }
    }

    public void addJson(JsonNode jsonNode) throws Exception {
        if (!checkID(jsonNode)) {
            System.out.println("ID not found");
            return;
        }

        String identifier = jsonNode.get("id").toString();
        if (FileUtil.checkExistence(SchemaFolderService.path + "\\" + identifier)){
            System.out.println("Folder " + identifier + " already exists");
        }
        else {
            folderController.createFolder(jsonNode);
            System.out.println("=====================================\n");
            System.out.println("- Adding Json to directory is done.");
        }
    }

    public void updateJson(JsonNode jsonNode, String key, Object value) throws Exception {
        if (!checkID(jsonNode)){
            System.out.println("ID not found");
            return;
        }
        String identifier = jsonNode.get("id").toString();
        if (!FileUtil.checkExistence(SchemaFolderService.path + "\\" + identifier)){
            System.out.println("This Json file does not exist in the directory");
            return;
        }

        if (jsonNode.has(key)){
            if (Objects.equals(key, "id")){
                System.out.println("Can't modify the Id of the Json");
            }
            else {
                JsonNode node = mapper.valueToTree(value);
                ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.put(key, node);
                deleteJson(jsonNode);
                folderController.createFolder(jsonNode);
                System.out.println("=====================================\n");
                System.out.println("- Updated the " + key + " to " + value);
            }
        }

        else{
            System.out.println("This key does not exist");
        }
    }
}