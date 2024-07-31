package com.example.SearchEngine.schema.util;

import com.example.SearchEngine.invertedIndex.TrieNode;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SchemaRoot {
    public static HashMap<String, TrieNode> roots = new HashMap<>();

    public static TrieNode getSchemaRoot(String schemaName) {
        if (!roots.containsKey(schemaName)) {
            roots.put(schemaName, new TrieNode());
        }
        return roots.get(schemaName);
    }

}
