package com.example.SearchEngine.schema.utils;

import com.example.SearchEngine.constants.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SchemaFormatter {

    private final HashMap<String, HashMap<String, Object>> defaultValues;

    public SchemaFormatter() throws IOException {
        this.defaultValues = new HashMap<>();
        HashMap<String, Object> defaults = readJSON(Constants.Paths.SCHEMA_FORMS_PATH + "defaultValues.json");
        for (String type : defaults.keySet()) {
            Object typeDefaults = defaults.get(type);
            this.defaultValues.put(type, (HashMap<String, Object>) typeDefaults);
        }
    }

    public void setGeneralDefaults(HashMap<String, Object> attribute) {
        Object typeDefaults = defaultValues.get("generalAttributeForm");
        if (typeDefaults instanceof HashMap) {
            for (String key : ((HashMap<String, Object>) typeDefaults).keySet()) {
                attribute.putIfAbsent(key, ((HashMap<String, Object>) typeDefaults).get(key));
            }
        }
    }

    public void setTypeDefaults(HashMap<String, Object> attribute) {
        String attributeType = (String) attribute.get("type");
        Object typeDefaults = defaultValues.get(attributeType);
        if (typeDefaults instanceof HashMap) {
            for (String key : ((HashMap<String, Object>) typeDefaults).keySet()) {
                attribute.putIfAbsent(key, ((HashMap<String, Object>) typeDefaults).get(key));
            }
        }
    }

    private HashMap<String, Object> readJSON(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        HashMap<String, Object> result = objectMapper.readValue(file, HashMap.class);
        return result;
    }
}
