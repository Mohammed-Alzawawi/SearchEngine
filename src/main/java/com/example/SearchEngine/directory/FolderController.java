package com.example.SearchEngine.directory;

import com.example.SearchEngine.path.PathController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class FolderController {
    public static String path = "C:\\Search Engine\\Directory";
    PathController pathController = new PathController();
    ObjectMapper mapper = new ObjectMapper();

    public void createFolder(JsonNode jsonNode) throws IOException {
        String folderPath = path + "\\" + jsonNode.get("id").toString();
        if (FileUtilities.createFolder(folderPath)){
            System.out.println("Done creating the folder");
            String jsonFilePath = folderPath + "\\" + jsonNode.get("id").toString() + "_Schema.json";
            String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            if (FileUtilities.createFile(jsonFilePath, content)){
                System.out.println("Done creating the Json File");
            }
            else{
                System.out.println("Problem with creating the Json File!");
            }

            if (pathController.createPath(jsonNode.get("id").toString(), path)){
                    System.out.println("Done creating the path file");
            }
            else {
                System.out.println("Problem with creating the path file!");
            }
        }
        else{
            System.out.println("Problem with creating the Folder!");
        }
    }

    public void deleteFolder(JsonNode jsonNode) throws IOException {
        String folderPath = path + "\\" + jsonNode.get("id").toString();
        if (FileUtilities.deleteFolder(folderPath)){
            if (pathController.deletePath(jsonNode.get("id").toString())){
                System.out.println("Done deleting the folder");
            }
            else{
                System.out.println("Problem with deleting the path file!");
            }
        }
        else{
            System.out.println("Problem with deleting the folder!");
        }
    }
}
