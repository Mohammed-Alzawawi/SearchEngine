package com.example.SearchEngine.invertedIndex.service;

import com.example.SearchEngine.Analyzers.Analyzer;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.invertedIndex.TrieInvertedIndex;
import com.example.SearchEngine.invertedIndex.TrieNode;
import com.example.SearchEngine.invertedIndex.utility.SchemaAnalyzer;
import com.example.SearchEngine.schema.util.SchemaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class TrieEngine implements InvertedIndexEngine {

    @Autowired
    TrieInvertedIndex trieInvertedIndex;
    @Autowired
    Ranker bm25Ranker;

    private List<Integer> gitRelevantDocuments(List<Token> tokens, TrieNode root, String schemaName) {
        HashMap<Integer, Double> documentsScores = new HashMap<>();

        for (Token token : tokens) {
            if (trieInvertedIndex.checkWordExist(root, token)) {
                TrieNode node = trieInvertedIndex.getWordLastNode(root, token);
                HashMap<Integer, HashMap<String, Double>> documents = node.getDocuments();
                HashMap<Integer, Double> currentScores = bm25Ranker.calculateScore(schemaName, documents);
                for (Integer documentId : currentScores.keySet()) {
                    documentsScores.put(documentId, documentsScores.getOrDefault(documentId, 0.0) + currentScores.get(documentId));
                }
            }
        }
        return bm25Ranker.rankDocuments(documentsScores);
    }


    @Override
    public List<Integer> search(String query, String schemaName) {
        Analyzer analyzer = SchemaAnalyzer.getAnalyzer(schemaName);
        List<Token> tokens = analyzer.analyze(query, 1.0);
        TrieNode root = SchemaRoot.getSchemaRoot(schemaName);
        return gitRelevantDocuments(tokens, root, schemaName);
    }
}
