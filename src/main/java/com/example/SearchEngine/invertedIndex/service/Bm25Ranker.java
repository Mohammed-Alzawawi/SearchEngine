package com.example.SearchEngine.invertedIndex.service;

import com.example.SearchEngine.invertedIndex.utility.CollectionInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Bm25Ranker implements Ranker {
    private final Double K = 2.0;
    private final Double B = 0.75;

    private Double Bm25Ranker(Double df, Double tf, Integer documentId, String schemaName) {
        Double N = Double.valueOf(CollectionInfo.getNumberOfDocument(schemaName));
        Double documentLength = CollectionInfo.getDocumentLength(schemaName, documentId);
        Double avgdl = CollectionInfo.getDocumentsTotalLength(schemaName) / N;
        Double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1);
        Double score = idf * tf * (K + 1) / (tf + K * (1 - B + B * documentLength / avgdl));
        return score;
    }


    @Override
    public HashMap<Integer, Double> calculateScore(String schemaName, HashMap<Integer, HashMap<String, Double>> documents) {
        HashMap<Integer, Double> currentDocumentsScores = new HashMap();
        for (int documentId : documents.keySet()) {
            Double df = (double) documents.size();
            Double tf = documents.get(documentId).get("total");
            currentDocumentsScores.put(documentId, Bm25Ranker(df, tf, documentId, schemaName));
        }
        return currentDocumentsScores;
    }

    @Override
    public List<Integer> rankDocuments(HashMap<Integer, Double> documentsScores) {

        List<Map.Entry<Integer, Double>> documents = new ArrayList<>(documentsScores.entrySet());
        Collections.sort(documents, (a, b) -> b.getValue().compareTo(a.getValue()));
        List<Integer> sortedKeys = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : documents) {
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }

}
