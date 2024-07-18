package com.example.SearchEngine.invertedIndex;

import java.util.Map;

public interface InvertedIndex {
    public void addDocument(String schemaName, Map<String, Object> document) throws Exception;

    public void deleteDocument(String schemaName, Integer documentId) throws Exception;
}
