package com.example.SearchEngine.directory;

import com.example.SearchEngine.path.PathController;
import org.json.simple.JSONObject;

import java.io.IOException;


public class FolderController {
    public static String path = "C:\\Search Engine\\Directory";
    PathController pathController = new PathController();


    public void createFolder(JSONObject jsonObject) throws IOException {
        String folderPath = path + "\\" + jsonObject.get("id").toString();
        if (FileUtilities.createFolder(folderPath)){
            System.out.println("Done creating the folder");
            String jsonFilePath = folderPath + "\\" + jsonObject.get("id").toString() + "_Schema.json";
            String content = jsonObject.toString();
            if (FileUtilities.createFile(jsonFilePath, content)){
                System.out.println("Done creating the Json File");
            }
            else{
                System.out.println("Problem with creating the Json File!");
            }

            if (pathController.createPath(jsonObject.get("id").toString(), path)){
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

    public void deleteFolder(JSONObject jsonObject) throws IOException {
        String folderPath = path + "\\" + jsonObject.get("id").toString();
        if (FileUtilities.deleteFolder(folderPath)){
            if (pathController.deletePath(jsonObject.get("id").toString())){
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
