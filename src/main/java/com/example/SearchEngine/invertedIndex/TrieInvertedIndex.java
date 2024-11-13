package com.example.SearchEngine.invertedIndex;

import com.example.SearchEngine.Analyzers.Analyzer;
import com.example.SearchEngine.invertedIndex.service.fuzzySearch.FuzzyTrie;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.invertedIndex.utility.CollectionInfo;
import com.example.SearchEngine.invertedIndex.utility.SchemaAnalyzer;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.documentFilter.DocumentFilterService;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TrieInvertedIndex implements InvertedIndex {

    @Autowired
    private SchemaDefaultService schemaDefaultService;
    @Autowired
    private SchemaPathService schemaPathService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private FuzzyTrie fuzzyTrie;
    @Autowired
    private DocumentFilterService documentFilterService;


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


    private void indexer(TrieNode root, Integer documentId, String fieldName, List<Token> tokens) {
        for (Token token : tokens) {
            TrieNode lastNode = getWordLastNode(root, token);
            lastNode.setEndOfTerm();
            lastNode.updateFieldWeight(fieldName, token.getWeight(), documentId);
        }
    }

    public void remover(List<Token> tokens, TrieNode root, Integer documentId) {
        for (Token token : tokens) {
            if (checkWordExist(root, token)) {
                TrieNode lastNode = getWordLastNode(root, token);
                lastNode.deleteDocument(documentId);
            }
        }
    }

    @Override
    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        TrieNode root = SchemaRoot.getSchemaRoot(schemaName);
        CollectionInfo.updateNumberOfDocument(schemaName, 1);
        for (String fieldName : document.keySet()) {
            List<Token> tokens = getTokens(schemaName, fieldName, document);
            if (!tokens.isEmpty()) {
                fuzzyTrie.addField((String)document.get(fieldName) , schemaName );
                CollectionInfo.addField(schemaName, (Integer) document.get("id"), tokens);
            }
            indexer(root, (Integer) document.get("id"), fieldName, tokens);
        }
    }

    @Override
    public void deleteDocument(String schemaName, Integer documentId) throws Exception {
        String path = schemaPathService.getSchemaPath(schemaName) + "documents/" + documentId;
        CollectionInfo.updateNumberOfDocument(schemaName, -1);
        Map<String, Object> document = mapper.readValue(new File(path), Map.class);
        TrieNode root = SchemaRoot.getSchemaRoot(schemaName);

        for (String fieldName : document.keySet()) {
            List<Token> tokens = getTokens(schemaName, fieldName, document);
            if (!tokens.isEmpty()) {
                fuzzyTrie.removeField((String)document.get(fieldName) , schemaName );
                CollectionInfo.removeField(schemaName, (Integer) document.get("id"), tokens);
            }
            remover(tokens, root, documentId);
        }
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
