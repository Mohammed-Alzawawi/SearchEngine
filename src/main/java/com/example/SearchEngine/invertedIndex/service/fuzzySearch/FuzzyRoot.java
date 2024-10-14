package com.example.SearchEngine.invertedIndex.service.fuzzySearch;


import org.springframework.stereotype.Service;

import java.util.HashMap;

public class FuzzyRoot {
    private static HashMap<String, FuzzyNode> roots = new HashMap<>();

    public static FuzzyNode getRoot(String schemaName) {
        if (!roots.containsKey(schemaName)) {
            roots.put(schemaName, new FuzzyNode());
        }
        return roots.get(schemaName);
    }
}
