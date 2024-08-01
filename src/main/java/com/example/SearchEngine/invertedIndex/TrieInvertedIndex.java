package com.example.SearchEngine.invertedIndex;

import com.example.SearchEngine.Analyzers.Analyzer;
import com.example.SearchEngine.Analyzers.AnalyzerEnum;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.invertedIndex.utility.Bm25Calculator;
import com.example.SearchEngine.invertedIndex.utility.CollectionInfo;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import com.example.SearchEngine.schema.util.SchemaRoot;
import com.example.SearchEngine.utils.storage.service.SchemaPathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TrieInvertedIndex implements InvertedIndex {

    @Autowired
    private SchemaDefaultService schemaDefaultService;
    @Autowired
    private SchemaPathService schemaPathService;
    @Autowired
    private SchemaRoot schemaRoot;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Bm25Calculator bm25Calculator;

    private Analyzer getFieldAnalyzer(Map<String, Object> schemaField) {
        return AnalyzerEnum.EnglishAnalyzer.getAnalyzer();
    }

    private boolean checkWordExist(TrieNode root, Token token) {
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

    private TrieNode getWordLastNode(TrieNode root, Token token){
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
            lastNode.updateFieldWeight("total", token.getWeight(), documentId);
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
        TrieNode root = schemaRoot.getSchemaRoot(schemaName);
        Map<String, Object> schema = (Map<String, Object>) schemaDefaultService.getSchema(schemaName).get("properties");

        for (String fieldName : document.keySet()) {
            List<Token> tokens = getTokens(fieldName, schema, document);
            if (!tokens.isEmpty()) {
                CollectionInfo.updateFieldTotalLength(schemaName , fieldName , (long) ((String) document.get(fieldName)).length());
                CollectionInfo.updateFieldTotalLength(schemaName , "total" , (long) ((String) document.get(fieldName)).length());
                CollectionInfo.updateDocumentLength(schemaName ,(Integer) document.get("id") , (long) ((String) document.get(fieldName)).length() , fieldName);
                CollectionInfo.updateDocumentLength(schemaName ,(Integer) document.get("id") , (long) ((String) document.get(fieldName)).length() , "total");
            }
            indexer(root, (Integer) document.get("id"), fieldName, tokens);
        }
    }

    @Override
    public void deleteDocument(String schemaName, Integer documentId) throws Exception {
        String path = schemaPathService.getSchemaPath(schemaName) + "documents/" + documentId;
        Map<String, Object> document = mapper.readValue(new File(path), Map.class);
        TrieNode root = schemaRoot.getSchemaRoot(schemaName);
        Map<String, Object> schema = (Map<String, Object>) schemaDefaultService.getSchema(schemaName).get("properties");

        for (String fieldName : document.keySet()) {
            List<Token> tokens = getTokens(fieldName, schema, document);
            remover(tokens, root, documentId);
        }
    }

    private List<Token> getTokens(String fieldName, Map<String, Object> schema, Map<String, Object> document) {
        Analyzer analyzer;
        List<Token> tokens = new ArrayList<>();
        if (!fieldName.equals("id")) {
            Map<String, Object> schemaField = (Map<String, Object>) schema.get(fieldName);
            if (schemaField.get("type").equals("text")) {
                analyzer = getFieldAnalyzer(schemaField);
                Double weight = 1.0;
                if (schemaField.containsKey("weight")) {
                    weight = (Double) schemaField.get("weight");
                }
                tokens = analyzer.analyze((String) document.get(fieldName), weight);
            }
        }
        return tokens;
    }


    public  Map<Integer , Double> search(String query , String schemaName ) {
        Map<Integer , Double> results = new HashMap<>();
        TrieNode root = schemaRoot.getSchemaRoot(schemaName);

        Analyzer analyzer = AnalyzerEnum.EnglishAnalyzer.getAnalyzer();
        List<Token> tokens = analyzer.analyze(query , 1.0) ;

        for (Token token : tokens) {
            TrieNode node = getWordLastNode(root, token);
            HashMap<Integer, HashMap<String, Double>> documents = node.getDocuments() ;
            Double df = (double) documents.size();
            for (Integer documentId : documents.keySet()) {
                HashMap<String , Double> fields = documents.get(documentId) ;
                for (String fieldName : fields.keySet()) {
                    results.put( documentId, results.getOrDefault(documentId , 0.0) + bm25Calculator.CalculateScore(schemaName ,fieldName , documentId , fields.get(fieldName) , df)) ;
                }
            }
        }
        return  results ;
    }

}
