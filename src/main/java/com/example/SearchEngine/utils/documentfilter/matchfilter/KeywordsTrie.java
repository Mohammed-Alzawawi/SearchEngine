package com.example.SearchEngine.utils.documentfilter.matchfilter;

import com.example.SearchEngine.schema.service.SchemaServiceInterface;
import com.example.SearchEngine.schema.utils.SchemaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class KeywordsTrie {

    @Autowired
    private SchemaServiceInterface schemaService;
    public boolean checkWordExistence(KeywordsNode root, String word) {
        KeywordsNode currentKeywordNode = root;
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            if (!currentKeywordNode.hasNextNode(c)) {
                return false;
            }
            currentKeywordNode = currentKeywordNode.getNextNode(c);
        }
        return true;
    }

    public KeywordsNode getWordsLastNode(KeywordsNode root, String word) {
        KeywordsNode currentKeywordsNode = root;
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            currentKeywordsNode = currentKeywordsNode.getNextNode(c);
        }
        return currentKeywordsNode;
    }

    public void addDocument(String schemaName, HashMap<String, Object> document) throws Exception {
        KeywordsNode root = SchemaRoot.getKeywordsSchemaRoot(schemaName);
        HashMap<String, Object> schema = schemaService.getSchema(schemaName);
        HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
        for (String fieldName : document.keySet()) {
            if (fieldName.equals("id")) {
                continue;
            }
            HashMap<String, Object> property = (HashMap<String, Object>) properties.get(fieldName);
            if (property.get("type").equals("keyword")) {
                KeywordsNode targetNode = getWordsLastNode(root, document.get(fieldName).toString());
                targetNode.addDocument(fieldName, (Long) document.get("id"));
            }
            else if (property.get("type").equals("array") && properties.get("items").equals("keyword")) {
                for (String word : (List<String>) document.get(fieldName)) {
                    KeywordsNode targetNode = getWordsLastNode(root, word);
                    targetNode.addDocument(fieldName, (Long) document.get("id"));
                }
            }
        }
    }

    public void deleteDocument(String schemaName, HashMap<String, Object> document) throws Exception {
        KeywordsNode root = SchemaRoot.getKeywordsSchemaRoot(schemaName);
        HashMap<String, Object> schema = schemaService.getSchema(schemaName);
        HashMap<String, Object> properties = (HashMap<String, Object>) schema.get("properties");
        for (String fieldName : document.keySet()) {
            if (fieldName.equals("id")) {
                continue;
            }
            HashMap<String, Object> property = (HashMap<String, Object>) properties.get(fieldName);
            if (property.get("type").equals("keyword")) {
                if (!checkWordExistence(root, document.get(fieldName).toString())) {
                    continue;
                }
                KeywordsNode targetNode = getWordsLastNode(root, document.get(fieldName).toString());
                targetNode.removeDocument(fieldName, (Long) document.get("id"));
            }
            else if (property.get("type").equals("array") && properties.get("items").equals("keyword")) {
                for (String word : (List<String>) document.get(fieldName)) {
                    if (!checkWordExistence(root, word)) {
                        continue;
                    }
                    KeywordsNode targetNode = getWordsLastNode(root, word);
                    targetNode.removeDocument(fieldName, (Long) document.get("id"));
                }
            }
        }
    }
}
