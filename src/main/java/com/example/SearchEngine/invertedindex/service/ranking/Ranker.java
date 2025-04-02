package com.example.SearchEngine.invertedindex.service.ranking;

import java.util.HashMap;
import java.util.List;

public interface Ranker {

    public HashMap<Long, Double> calculateScore(String schemaName, HashMap<Long, HashMap<String, Double>> documents);

    public List<Long> rankDocuments(HashMap<Long, Double> documentsScores);
}
