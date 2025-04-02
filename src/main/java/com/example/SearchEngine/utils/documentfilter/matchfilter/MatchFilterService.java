package com.example.SearchEngine.utils.documentfilter.matchfilter;

import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.schema.utils.SchemaRoot;
import com.example.SearchEngine.utils.documentfilter.Merger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchFilterService {

    @Autowired
    private KeywordsTrie keywordsTrie;
    @Autowired
    private Merger merger;

    public List<Long> getFilteredDocuments(String schemaName, HashMap<String, String> keywords) {
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
        KeywordsNode root = SchemaRoot.getKeywordsSchemaRoot(schemaName);
        for (String fieldName : keywords.keySet()) {
            String word = keywords.get(fieldName);
            if (!keywordsTrie.checkWordExistence(root, word)) {
                filteredDocuments = new ArrayList<>();
                return filteredDocuments;
            }
            List<Long> documentsInMatch = new ArrayList<>(keywordsTrie.getWordsLastNode(root, word)
                    .getDocuments()
                    .getOrDefault(fieldName, new HashSet<>()));
            Collections.sort(documentsInMatch);
            filteredDocuments = merger.mergeTwoLists(documentsInMatch, filteredDocuments);
        }
        return filteredDocuments;
    }
}
