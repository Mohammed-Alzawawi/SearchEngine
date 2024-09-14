package com.example.SearchEngine.invertedIndex.service;

import java.util.List;

public interface InvertedIndexEngine {
    public List<Object> search(String query, String schemaName) throws Exception;
}
