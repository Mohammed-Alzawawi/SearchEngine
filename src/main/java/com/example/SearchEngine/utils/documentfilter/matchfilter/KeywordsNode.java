package com.example.SearchEngine.utils.documentfilter.matchfilter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class KeywordsNode implements Serializable {

    private static final long serialVersionUID = 5424601767160570951L;
    private HashMap<Character, KeywordsNode> nextNodes = new HashMap<>();
    private boolean endOfTerm = false;
    private HashMap<String, HashSet<Long>> documents = new HashMap<>();

    public KeywordsNode() {
    }

    public HashMap<Character, KeywordsNode> getNextNodes() {
        return nextNodes;
    }

    public KeywordsNode getNextNode(Character c) {
        if (hasNextNode(c)) {
            return nextNodes.get(c);
        }
        nextNodes.put(c, new KeywordsNode());
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

    public void addDocument(String fieldName, Long documentID) {
        if (!this.documents.containsKey(fieldName)) {
            this.documents.put(fieldName, new HashSet<>());
        }
        this.documents.get(fieldName).add(documentID);
    }

    public void removeDocument(String fieldName, Long documentID) {
        if (!this.documents.containsKey(fieldName)) {
            return;
        }
        if (this.documents.get(fieldName).contains(documentID)) {
            this.documents.get(fieldName).remove(documentID);
        }
    }
    public HashMap<String, HashSet<Long>> getDocuments() {
        return documents;
    }
}
