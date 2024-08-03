package com.example.SearchEngine.invertedIndex.service;

import java.util.List;

public interface InvertedIndexEngine {
    public List<Integer> search(String query, String schemaName);
}
