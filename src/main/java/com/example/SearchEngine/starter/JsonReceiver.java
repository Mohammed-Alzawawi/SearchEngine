package com.example.SearchEngine.starter;

import com.example.SearchEngine.directoryControl.FolderManipulator;
import com.example.SearchEngine.jsonHandler.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class JsonReceiver {
    private final FolderManipulator folderManipulator = new FolderManipulator();

    public void start() throws IOException, ParseException {
        String filePath = "C:\\Search Engine\\Services\\centuryBoys.json";
        JSONObject jsonObject = JsonParser.jsonFileToJsonObject(filePath);
//        addJson(jsonObject);
//        updateJson(jsonObject, "name", "Century Boys");
//        deleteJson(jsonObject);
    }


    public boolean checkID(JSONObject jsonObject){
        return jsonObject.containsKey("id") && jsonObject.get("id") instanceof Long;
    }

    public void deleteJson(JSONObject jsonObject){
        if (checkID(jsonObject)) {
            folderManipulator.deleteFolder(jsonObject.get("id").toString());
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
        if (folderManipulator.checkExistence(identifier)){
            System.out.println("Folder " + identifier + " already exists");
        }
        else {
            folderManipulator.createFolder(jsonObject);
            System.out.println("=====================================\n");
            System.out.println("- Adding Json to directory is done.");
        }
    }

    public void updateJson(JSONObject jsonObject, String key, Object value) throws IOException {
        if (!checkID(jsonObject)){
            System.out.println("ID not found");
            return;
        }

        if (!folderManipulator.checkExistence(jsonObject.get("id").toString())){
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
                folderManipulator.createFolder(jsonObject);
                System.out.println("=====================================\n");
                System.out.println("- Updated the " + key + " to " + value);
            }
        }

        else{
            System.out.println("This key does not exist");
        }
    }
}