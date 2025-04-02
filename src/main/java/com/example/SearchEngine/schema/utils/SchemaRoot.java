package com.example.SearchEngine.schema.utils;

import com.example.SearchEngine.invertedindex.TrieNode;
import com.example.SearchEngine.utils.documentfilter.matchfilter.KeywordsNode;

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
