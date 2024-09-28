package com.example.SearchEngine.schema.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SchemaServiceInterface {
    public void addNewSchema(HashMap<String, Object> jsonObject) throws Exception;

    public HashMap<String, Object> getSchema(String schemaName) throws Exception;
    public List<String> getSchemasNames();
}
