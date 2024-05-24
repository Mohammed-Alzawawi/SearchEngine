package com.example.SearchEngine.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JsonParser {
    private static String jsonFileToString(String fileName) throws IOException {
        String json = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = "";

        while((line = bufferedReader.readLine()) != null){
            json += line + '\n';
        }
        return json;
    }

    public static JSONObject jsonFileToJsonObject(String filepath) throws IOException, ParseException {
        String str = jsonFileToString(filepath);
        JSONObject jsonObject = null;
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(str);
        jsonObject = (JSONObject) obj;

        return jsonObject;
    }
}