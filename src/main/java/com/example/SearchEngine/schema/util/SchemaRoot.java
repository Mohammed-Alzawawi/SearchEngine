package com.example.SearchEngine.schema.util;

import com.example.SearchEngine.invertedIndex.Node;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SchemaRoot {
    private static HashMap<String, Node> roots = new HashMap<>();

    public Node getSchemaRoot(String schemaName) {
        if (!roots.containsKey(schemaName)) {
            roots.put(schemaName, new Node());
        }
        return roots.get(schemaName);
    }

}
