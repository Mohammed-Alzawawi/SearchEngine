package com.example.SearchEngine.invertedindex.service.search;

import com.example.SearchEngine.analyzers.Analyzer;
import com.example.SearchEngine.analyzers.AnalyzerEnum;
import com.example.SearchEngine.document.service.DocumentStorageService;
import com.example.SearchEngine.invertedindex.TrieInvertedIndex;
import com.example.SearchEngine.invertedindex.TrieNode;
import com.example.SearchEngine.invertedindex.service.fuzzysearch.FuzzyTrie;
import com.example.SearchEngine.invertedindex.service.query.QueryValidator;
import com.example.SearchEngine.invertedindex.service.ranking.Ranker;
import com.example.SearchEngine.invertedindex.utils.SchemaAnalyzer;
import com.example.SearchEngine.schema.utils.SchemaRoot;
import com.example.SearchEngine.tokenization.Token;
import com.example.SearchEngine.utils.documentfilter.DocumentFilterService;

import com.example.SearchEngine.utils.documentfilter.Merger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class TrieEngine implements InvertedIndexEngine {

    @Autowired
    TrieInvertedIndex trieInvertedIndex;
    @Autowired
    Ranker bm25Ranker;
    @Autowired
    DocumentStorageService documentStorageService;
    @Autowired
    FuzzyTrie fuzzyTrie;
    @Autowired
    QueryValidator queryValidator;
    @Autowired
    DocumentFilterService documentFilterService;
    @Autowired
    Merger merger;

    private List<Long> getRelevantDocuments(List<Token> tokens, TrieNode root, String schemaName) {
        HashMap<Long, Double> documentsScores = new HashMap<>();

        for (Token token : tokens) {
            if (trieInvertedIndex.checkWordExist(root, token)) {
                TrieNode node = trieInvertedIndex.getWordLastNode(root, token);
                HashMap<Long, HashMap<String, Double>> documents = node.getDocuments();
                HashMap<Long, Double> currentScores = bm25Ranker.calculateScore(schemaName, documents);
                for (Long documentId : currentScores.keySet()) {
                    documentsScores.put(documentId, documentsScores.getOrDefault(documentId, 0.0) + currentScores.get(documentId));
                }
            }
        }
        return bm25Ranker.rankDocuments(documentsScores);
    }


    @Override
    public List<Object> search(HashMap<String, Object> query, String schemaName) throws Exception {
        if (!queryValidator.validate(query, schemaName)) {
            throw new IllegalStateException("Non valid search query");
        }
        Analyzer analyzer = AnalyzerEnum.DefaultAnalyzer.getAnalyzer();
        List<Token> tokens = analyzer.analyze((String) query.get("query"), 1.0);
        List<String> words = new ArrayList<>();
        tokens.forEach(token -> {
            words.addAll(fuzzyTrie.findMostSimilarWord(token.getWord(), schemaName));
        });

        tokens.clear();
        Analyzer schemaAnalyzer = SchemaAnalyzer.getAnalyzer(schemaName);
        words.forEach(word -> {
            schemaAnalyzer.analyze(word, 1.0).forEach(token -> {
                tokens.add(token);
            });
        });
        TrieNode root = SchemaRoot.getInvertedIndexSchemaRoot(schemaName);
        List<Long> documentsId = getRelevantDocuments(tokens, root, schemaName);
        List<Object> relevantDocuments = new ArrayList<>();
        List<Long> filteredDocumentIDs = documentFilterService.getDocuments(schemaName, (HashMap<String, Object>) query.get("filters"));
        Collections.sort(documentsId);
//        Collections.sort(filteredDocumentIDs);
        if (!documentsId.isEmpty()) {
            documentsId = merger.mergeTwoLists(documentsId, filteredDocumentIDs);
        }
        for (Long documentId : documentsId) {
            relevantDocuments.add(documentStorageService.getDocument(schemaName, documentId));
        }
        return relevantDocuments;
    }
}
