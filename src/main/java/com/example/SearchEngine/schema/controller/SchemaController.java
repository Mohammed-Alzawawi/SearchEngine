package com.example.SearchEngine.schema.controller;

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
    @Autowired
    private SchemaServiceInterface schemaService;
    @Autowired
    ObjectMapper mapper;

    @PostMapping("createSchema")
    public void createSchema(@RequestBody String jsonString) throws Exception {
        HashMap<String, Object> schema = jsonStringToJsonObject(jsonString);
        schemaService.addNewSchema(schema);
    }

    private HashMap<String, Object> jsonStringToJsonObject(String jsonString) throws JsonProcessingException {
        HashMap<String, Object> result = mapper.readValue(jsonString.toString(), HashMap.class);
        return result;
    }
}
