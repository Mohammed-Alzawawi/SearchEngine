package com.example.SearchEngine.invertedindex;

import java.io.Serializable;
import java.util.HashMap;

public class TrieNode implements Serializable {
    private static final long serialVersionUID = 5424601767160570951L;
    private HashMap<Character, TrieNode> nextNodes = new HashMap<>();
    private boolean endOfTerm = false;
    private HashMap<Long, HashMap<String, Double>> documents = new HashMap<>();

    public TrieNode() {
    }

    public HashMap<Character, TrieNode> getNextNodes() {
        return nextNodes;
    }

    public TrieNode getNextNode(Character c) {
        if (hasNextNode(c)) {
            return nextNodes.get(c);
        }
        nextNodes.put(c, new TrieNode());
        return nextNodes.get(c);
    }

    public boolean hasNextNode(Character c) {
        return nextNodes.containsKey(c);
    }

    public boolean empty() {
        return documents.isEmpty();
    }

    public boolean isEndOfTerm() {
        return endOfTerm;
    }

    public void setEndOfTerm() {
        endOfTerm = true;
    }

    public void removeEndOfTerm() {
        endOfTerm = false;
    }

    public void updateFieldWeight(String fieldName, Double weight, Long documentId) {
        if (!documents.containsKey(documentId)) {
            documents.put(documentId, new HashMap<>());
        }
        if (!documents.get(documentId).containsKey(fieldName)) {
            documents.get(documentId).put(fieldName, 0.0);
        }
        if (!documents.get(documentId).containsKey("total")) {
            documents.get(documentId).put("total", 0.0);
        }
        documents.get(documentId).put(fieldName, documents.get(documentId).get(fieldName) + weight);
        documents.get(documentId).put("total", documents.get(documentId).get("total") + weight);
        if (documents.get(documentId).get(fieldName)==0) {
            documents.get(documentId).remove(fieldName);
        }
        if (documents.get(documentId).size()==1) {
            documents.remove(documentId);
        }
    }

    public void deleteDocument(Long documentId) {
        if (documents.containsKey(documentId)) {
            documents.remove(documentId);
        }
        if (empty()){
            removeEndOfTerm();
        }
    }

    public HashMap<Long, HashMap<String, Double>> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return "nextNodes=" + nextNodes + "\n, documents=" + documents+'\n' ;
    }
}

