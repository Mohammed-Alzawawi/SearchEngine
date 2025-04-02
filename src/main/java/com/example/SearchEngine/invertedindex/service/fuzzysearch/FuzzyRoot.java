package com.example.SearchEngine.invertedindex.service.fuzzysearch;


import java.util.HashMap;

public class FuzzyRoot {
    public static HashMap<String, FuzzyNode> roots = new HashMap<>();

    public static FuzzyNode getRoot(String schemaName) {
        if (!roots.containsKey(schemaName)) {
            roots.put(schemaName, new FuzzyNode());
        }
        return roots.get(schemaName);
    }
}
