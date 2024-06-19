package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.utils.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchemaFolderService {
    public static String path = "C:\\Search Engine\\Directory";
    @Autowired
    private SchemaPathService schemaPathService;
    @Autowired
    private ObjectMapper mapper;

    public void createFolder(JsonNode jsonNode) throws Exception {
        String folderPath = path + "\\" + jsonNode.get("id").toString();
        if (FileUtil.createFolder(folderPath)){
            System.out.println("Done creating the folder");
            String jsonFilePath = folderPath + "\\" + jsonNode.get("id").toString() + "_Schema.json";
            String content = null;
            try {
                content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error writing json file");
            }

            if (FileUtil.createFile(jsonFilePath, content)){
                System.out.println("Done creating the Json File");
            }
            else{
                System.out.println("Problem with creating the Json File!");
            }

            if (schemaPathService.createPath(jsonNode.get("id").toString(), path)){
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

    public void deleteFolder(JsonNode jsonNode) throws Exception {
        String folderPath = path + "\\" + jsonNode.get("id").toString();
        if (FileUtil.deleteFolder(folderPath)){
            if (schemaPathService.deletePath(jsonNode.get("id").toString())){
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
