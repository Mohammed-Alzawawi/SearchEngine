package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.schema.utils.SchemaValidator;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import com.example.SearchEngine.utils.storage.service.SchemaStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SchemaDefaultService implements SchemaServiceInterface {
    @Autowired
    private SchemaValidator schemaValidator;
    @Autowired
    private SchemaStorageService schemaStorageService;
    @Autowired
    private SchemaPathService schemaPathService;
    @Autowired
    private ObjectMapper mapper;

    private List<String> schemasNames = new ArrayList<>();

    public void addNewSchema(HashMap<String, Object> jsonObject) throws Exception {
        schemaValidator.validateSchema(jsonObject);
        JsonNode jsonNode = mapper.convertValue(jsonObject, JsonNode.class);
        schemaStorageService.saveSchemaFile(jsonNode);
        schemasNames.add(jsonNode.get("name").toString());
    }

    public HashMap<String, Object> getSchema(String schemaName) throws Exception {
        String schemaPath;
        schemaPath = schemaPathService.getSchemaPath(schemaName) + schemaName + "_Schema.json";
        HashMap<String, Object> schema = mapper.readValue(new File(schemaPath), HashMap.class);
        return schema;
    }

    public List<String> getSchemasNames() {
        return schemasNames;
    }
}
