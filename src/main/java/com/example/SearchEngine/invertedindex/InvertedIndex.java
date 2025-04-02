package com.example.SearchEngine.invertedindex;

import java.util.Map;

public interface InvertedIndex {
    public void addDocument(String schemaName, Map<String, Object> document) throws Exception;

    public void deleteDocument(String schemaName, Map<String, Object> document) throws Exception;

    public void addField(String schemaName, Map<String, Object> document, String fieldName) throws Exception ;

    public void removeField(String schemaName, Map<String, Object> document, String fieldName) throws Exception;
}
