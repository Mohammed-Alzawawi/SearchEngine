package com.example.SearchEngine.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonParser {
    private static String jsonFileToString(String filePath) throws IOException {
        String json = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line = "";

        while((line = bufferedReader.readLine()) != null){
            json += line + '\n';
        }
        return json;
    }

    public static JsonNode jsonFileToJsonNode(String filepath) throws IOException {
        String str = jsonFileToString(filepath);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(str);
    }
}