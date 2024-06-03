package com.example.SearchEngine.schema.service;

import com.example.SearchEngine.schema.util.SchemaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultService implements SchemaServiceInterface {
    @Autowired
    private SchemaValidator schemaValidator;

    public void addNewSchema(HashMap<String, Object> jsonObject) {
        schemaValidator.validateSchema(jsonObject);
//        storageService.saveNewSchema(jsonObject);
    }
}
