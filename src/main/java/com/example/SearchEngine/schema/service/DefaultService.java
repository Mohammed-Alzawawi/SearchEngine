package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.schema.util.SchemaValidator;
import com.example.SearchEngine.utils.storage.service.SchemaStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultService implements SchemaServiceInterface {
    @Autowired
    private SchemaValidator schemaValidator;
    @Autowired
    private SchemaStorageService schemaStorageService;
    @Autowired
    private ObjectMapper mapper;

    public void addNewSchema(HashMap<String, Object> jsonObject) throws Exception {
        schemaValidator.validateSchema(jsonObject);
        JsonNode jsonNode = mapper.convertValue(jsonObject, JsonNode.class);
        schemaStorageService.saveSchemaFile(jsonNode);
    }
}
