package com.example.SearchEngine.invertedIndex;

import com.example.SearchEngine.Analyzers.Analyzer;
import com.example.SearchEngine.Analyzers.AnalyzerEnum;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.schema.service.SchemaDefaultService;
import com.example.SearchEngine.schema.util.SchemaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class TrieInvertedIndex implements InvertedIndex {

    @Autowired
    private SchemaDefaultService schemaDefaultService;
    @Autowired
    private SchemaRoot schemaRoot;

    private Analyzer getFieldAnalyzer(Map<String, Object> schemaField) {
        return AnalyzerEnum.EnglishAnalyzer.getAnalyzer();
    }

    private void indexer(Node root, Integer documentId, String fieldName, List<Token> tokens) {
        for (Token token : tokens) {
            Node currentNode = root;
            for (int i = 0; i < token.getWord().length(); i++) {
                Character c = token.getWord().charAt(i);
                currentNode = currentNode.getNextNode(c);
            }
            currentNode.setEndOfTerm();
            currentNode.updateFieldWeight(fieldName, token.getWeight(), documentId);
        }
    }

    @Override
    public void addDocument(String schemaName, Map<String, Object> document) throws Exception {
        Analyzer analyzer;
        List<Token> tokens;
        Node root = schemaRoot.getSchemaRoot(schemaName);
        Map<String, Object> schema = (Map<String, Object>) schemaDefaultService.getSchema(schemaName).get("properties");

        for (String fieldName : document.keySet()) {
            if (fieldName.equals("id")) {
                continue;
            }
            Map<String, Object> schemaField = (Map<String, Object>) schema.get(fieldName);
            if (schemaField.get("type").equals("text")) {
                analyzer = getFieldAnalyzer(schemaField);
                Double weight = 1.0;
                if (schemaField.containsKey("weight")) {
                    weight = (Double) schemaField.get("weight");
                }
                tokens = analyzer.analyze((String) document.get(fieldName), weight);
                indexer(root, (Integer) document.get("id"), fieldName, tokens);
            }
        }
    }

    @Override
    public void deleteDocument(String schemaName, Integer documentId) throws Exception {
        Node root = schemaRoot.getSchemaRoot(schemaName);
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node currnetNode = stack.pop();
            if (currnetNode.isEndOfTerm()) {
                currnetNode.deleteDocument(documentId);
                if (currnetNode.empty()) {
                    currnetNode.removeEndOfTerm();
                }
            }
            Map<Character, Node> nextNodes = currnetNode.getNextNodes();
            for (Character character : nextNodes.keySet()) {
                stack.push(nextNodes.get(character));
            }
        }
    }

}
