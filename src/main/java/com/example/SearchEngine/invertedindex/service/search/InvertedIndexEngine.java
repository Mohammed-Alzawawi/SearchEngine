package com.example.SearchEngine.invertedindex.service.search;

import java.util.HashMap;
import java.util.List;

public interface InvertedIndexEngine {
    public List<Object> search(HashMap<String, Object> query, String schemaName) throws Exception;
}
