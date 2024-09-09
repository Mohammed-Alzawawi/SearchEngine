package com.example.SearchEngine.invertedIndex.service;

import java.util.HashMap;
import java.util.List;

public interface Ranker {

    public HashMap<Integer, Double> calculateScore(String schemaName, HashMap<Integer, HashMap<String, Double>> documents);

    public List<Integer> rankDocuments(HashMap<Integer, Double> documentsScores);
}
