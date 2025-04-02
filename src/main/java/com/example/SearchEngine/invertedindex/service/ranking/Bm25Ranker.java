package com.example.SearchEngine.invertedindex.service.ranking;

import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Bm25Ranker implements Ranker {
    private final Double K = 2.0;
    private final Double B = 0.75;

    private Double Bm25Ranker(Double df, Double tf, Long documentId, String schemaName) {
        Double N = Double.valueOf(CollectionInfo.getNumberOfDocument(schemaName));
        Double documentLength = CollectionInfo.getDocumentLength(schemaName, documentId);
        Double averageDocumentLength = CollectionInfo.getDocumentsTotalLength(schemaName) / N;
        Double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1);
        Double score = idf * tf * (K + 1) / (tf + K * (1 - B + B * documentLength / averageDocumentLength));
        return score;
    }


    @Override
    public HashMap<Long, Double> calculateScore(String schemaName, HashMap<Long, HashMap<String, Double>> documents) {
        HashMap<Long, Double> currentDocumentsScores = new HashMap();
        for (Long documentId : documents.keySet()) {
            Double df = (double) documents.size();
            Double tf = documents.get(documentId).get("total");
            currentDocumentsScores.put(documentId, Bm25Ranker(df, tf, documentId, schemaName));
        }
        return currentDocumentsScores;
    }

    @Override
    public List<Long> rankDocuments(HashMap<Long, Double> documentsScores) {

        List<Map.Entry<Long, Double>> documents = new ArrayList<>(documentsScores.entrySet());
        Collections.sort(documents, (a, b) -> b.getValue().compareTo(a.getValue()));
        List<Long> sortedKeys = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : documents) {
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }

}
