package com.example.SearchEngine.json;

import com.example.SearchEngine.directory.FileUtilities;
import com.example.SearchEngine.directory.FolderController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Objects;

public class JsonReceiver {
    private final FolderController folderController = new FolderController();
    ObjectMapper mapper = new ObjectMapper();

    public void start() throws IOException {
        String filePath = "C:\\Search Engine\\Services\\centuryBoys.json";
        JsonNode jsonNode = JsonParser.jsonFileToJsonNode(filePath);
        System.out.println(jsonNode);
//        addJson(jsonNode);
//        updateJson(jsonNode, "name", "Century Boys {Updated}");
//        deleteJson(jsonNode);
    }


    public boolean checkID(JsonNode jsonNode){
        return jsonNode.has("id") && (jsonNode.get("id").isInt() || jsonNode.get("id").isLong());
    }

    public void deleteJson(JsonNode jsonNode) throws IOException {
        if (checkID(jsonNode)) {
            // check folderController
            folderController.deleteFolder(jsonNode);
        }
        else{
            System.out.println("ID not found");
        }
    }

    public void addJson(JsonNode jsonNode) throws IOException {
        if (!checkID(jsonNode)) {
            System.out.println("ID not found");
            return;
        }

        String identifier = jsonNode.get("id").toString();
        if (FileUtilities.checkExistence(FolderController.path + "\\" + identifier)){
            System.out.println("Folder " + identifier + " already exists");
        }
        else {
            folderController.createFolder(jsonNode);
            System.out.println("=====================================\n");
            System.out.println("- Adding Json to directory is done.");
        }
    }

    public void updateJson(JsonNode jsonNode, String key, Object value) throws IOException {
        if (!checkID(jsonNode)){
            System.out.println("ID not found");
            return;
        }
        String identifier = jsonNode.get("id").toString();
        if (!FileUtilities.checkExistence(FolderController.path + "\\" + identifier)){
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