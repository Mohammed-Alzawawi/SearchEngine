package com.example.SearchEngine.utils.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonParserUtil {
    private static String jsonFileToString(String filePath) throws Exception {
        String json = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        String line = "";

        while(true){
            try {
                if ((line = bufferedReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading file");
            }
            json += line + '\n';
        }
        return json;
    }

    public static JsonNode jsonFileToJsonNode(String filepath) throws Exception {
        String str = jsonFileToString(filepath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(str);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error reading file");
        }
    }
}