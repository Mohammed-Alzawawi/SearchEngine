package com.example.SearchEngine.invertedindex;

import com.example.SearchEngine.analyzers.Analyzer;
import com.example.SearchEngine.tokenization.Token;
import com.example.SearchEngine.invertedindex.service.fuzzysearch.FuzzyTrie;
import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.invertedindex.utils.SchemaAnalyzer;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import com.example.SearchEngine.schema.utils.SchemaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrieInvertedIndex implements InvertedIndex {

    @Autowired
    private FuzzyTrie fuzzyTrie;
    @Autowired
    private SchemaDefaultService schemaDefaultService;


    public boolean checkWordExist(TrieNode root, Token token) {
        TrieNode currentTrieNode = root;
        for (int i = 0; i < token.getWord().length(); i++) {
            Character c = token.getWord().charAt(i);
            if (!currentTrieNode.hasNextNode(c)) {
                return false;
            }
            currentTrieNode = currentTrieNode.getNextNode(c);
        }
        return true;
    }

    public TrieNode getWordLastNode(TrieNode root, Token token) {
        TrieNode currentTrieNode = root;
        for (int i = 0; i < token.getWord().length(); i++) {
            Character c = token.getWord().charAt(i);
            currentTrieNode = currentTrieNode.getNextNode(c);
        }
        return currentTrieNode;
    }


    private void indexer(TrieNode root, Long documentId, String fieldName, List<Token> tokens) {
        for (Token token : tokens) {
            TrieNode lastNode = getWordLastNode(root, token);
            lastNode.setEndOfTerm();
            lastNode.updateFieldWeight(fieldName, token.getWeight(), documentId);
        }
    }

    public void remover(TrieNode root, Long documentId, String fieldName, List<Token> tokens) {
        for (Token token : tokens) {
            if (checkWordExist(root, token)) {
                TrieNode lastNode = getWordLastNode(root, token);
                lastNode.updateFieldWeight(fieldName, -token.getWeight(), documentId);
            }
        }
    }

    @Override
    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        CollectionInfo.insertDocument(schemaName, (Long) document.get("id"));
        for (String fieldName : document.keySet()) {
            addField(schemaName, document, fieldName);
        }
    }

    @Override
    public void addField(String schemaName, Map<String, Object> document, String fieldName) throws Exception {
        TrieNode root = SchemaRoot.getInvertedIndexSchemaRoot(schemaName);
        List<Token> tokens = getTokens(schemaName, fieldName, document);
        if (!tokens.isEmpty()) {
            fuzzyTrie.addField((String) document.get(fieldName), schemaName);
            CollectionInfo.addField(schemaName, (Long) document.get("id"), tokens);
        }
        indexer(root, (Long) document.get("id"), fieldName, tokens);
    }

    @Override
    public void deleteDocument(String schemaName, Map<String, Object> document) throws Exception {
        CollectionInfo.removeDocument(schemaName, (Long) document.get("id"));
        for (String fieldName : document.keySet()) {
            removeField(schemaName, document, fieldName);
        }
    }

    @Override
    public void removeField(String schemaName, Map<String, Object> document, String fieldName) throws Exception {
        TrieNode root = SchemaRoot.getInvertedIndexSchemaRoot(schemaName);
        List<Token> tokens = getTokens(schemaName, fieldName, document);
        if (!tokens.isEmpty()) {
            fuzzyTrie.removeField((String) document.get(fieldName), schemaName);
            CollectionInfo.removeField(schemaName, (Long) document.get("id"), tokens);
        }
        remover(root, (Long) document.get("id") , fieldName , tokens);
    }

    private List<Token> getTokens(String schemaName, String fieldName, Map<String, Object> document) throws Exception {
        List<Token> tokens = new ArrayList<>();
        Analyzer analyzer = SchemaAnalyzer.getAnalyzer(schemaName);
        Map<String, Object> schema = (Map<String, Object>) schemaDefaultService.getSchema(schemaName).get("properties");

        if (!fieldName.equals("id")) {
            Map<String, Object> schemaField = (Map<String, Object>) schema.get(fieldName);
            if (schemaField.get("type").equals("text")) {
                Double weight = 1.0;
                if (schemaField.containsKey("weight")) {
                    weight = (Double) schemaField.get("weight");
                }
                tokens = analyzer.analyze((String) document.get(fieldName), weight);
            }
        }
        return tokens;
    }

}