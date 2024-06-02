package com.example.SearchEngine.schema;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SchemaService {

    private final SchemaValidator schemaValidator;
    public SchemaService() throws IOException {
        schemaValidator = new SchemaValidator();
    }

    public void addNewSchema(HashMap<String, Object> jsonObject) {
        schemaValidator.validateSchema(jsonObject);
//        storageService.saveNewSchema(jsonObject);
    }
}
