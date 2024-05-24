package com.example.SearchEngine.json;

import com.example.SearchEngine.directory.FileUtilities;
import com.example.SearchEngine.directory.FolderController;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class JsonReceiver {
    private final FolderController folderController = new FolderController();

    public void start() throws IOException, ParseException {
        String filePath = "C:\\Search Engine\\Services\\centuryBoys.json";
        JSONObject jsonObject = JsonParser.jsonFileToJsonObject(filePath);
//        addJson(jsonObject);
//        updateJson(jsonObject, "name", "Century Boys {Updated}");
//        deleteJson(jsonObject);
    }


    public boolean checkID(JSONObject jsonObject){
        return jsonObject.containsKey("id") && jsonObject.get("id") instanceof Long;
    }

    public void deleteJson(JSONObject jsonObject) throws IOException {
        if (checkID(jsonObject)) {
            folderController.deleteFolder(jsonObject);
        }
        else{
            System.out.println("ID not found");
        }
    }

    public void addJson(JSONObject jsonObject) throws IOException {
        if (!checkID(jsonObject)) {
            System.out.println("ID not found");
            return;
        }

        String identifier = jsonObject.get("id").toString();
        if (FileUtilities.checkExistence(FolderController.path + "\\" + identifier)){
            System.out.println("Folder " + identifier + " already exists");
        }
        else {
            folderController.createFolder(jsonObject);
            System.out.println("=====================================\n");
            System.out.println("- Adding Json to directory is done.");
        }
    }

    public void updateJson(JSONObject jsonObject, String key, Object value) throws IOException {
        if (!checkID(jsonObject)){
            System.out.println("ID not found");
            return;
        }
        String identifier = jsonObject.get("id").toString();
        if (!FileUtilities.checkExistence(FolderController.path + "\\" + identifier)){
            System.out.println("This Json file does not exist in the directory");
            return;
        }

        if (jsonObject.containsKey(key)){
            if (Objects.equals(key, "id")){
                System.out.println("Can't modify the Id of the Json");
            }
            else {
                jsonObject.put(key, value);
                deleteJson(jsonObject);
                folderController.createFolder(jsonObject);
                System.out.println("=====================================\n");
                System.out.println("- Updated the " + key + " to " + value);
            }
        }

        else{
            System.out.println("This key does not exist");
        }
    }
}