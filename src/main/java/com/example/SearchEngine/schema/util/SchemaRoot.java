package com.example.SearchEngine.schema.util;

import com.example.SearchEngine.invertedIndex.TrieNode;
import com.example.SearchEngine.utils.documentFilter.matchFilter.KeywordsNode;

import java.util.HashMap;

public class SchemaRoot {
    public static HashMap<String, TrieNode> invertedIndexRoots = new HashMap<>();
    public static HashMap<String, KeywordsNode> keywordsRoots = new HashMap<>();

    public static TrieNode getInvertedIndexSchemaRoot(String schemaName) {
        if (!invertedIndexRoots.containsKey(schemaName)) {
            invertedIndexRoots.put(schemaName, new TrieNode());
        }
        return invertedIndexRoots.get(schemaName);
    }

    public static KeywordsNode getKeywordsSchemaRoot(String schemaName) {
        if (!keywordsRoots.containsKey(schemaName)) {
            keywordsRoots.put(schemaName, new KeywordsNode());
        }
        return keywordsRoots.get(schemaName);
    }
}
