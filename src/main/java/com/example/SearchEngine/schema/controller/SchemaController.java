package com.example.SearchEngine.schema.controller;

import com.example.SearchEngine.schema.service.DefaultService;
import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(path = "api/schema")
public class SchemaController {
    private SchemaServiceInterface schemaService = new DefaultService();

    public SchemaController(SchemaServiceInterface schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping("createSchema")
    public void createSchema(@RequestBody String jsonString) throws JsonProcessingException {
        HashMap<String, Object> schema = jsonStringToJsonObject(jsonString);
        schemaService.addNewSchema(schema);
    }

    private HashMap<String, Object> jsonStringToJsonObject(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> result = objectMapper.readValue(jsonString.toString(), HashMap.class);
        return result;
    }
}
