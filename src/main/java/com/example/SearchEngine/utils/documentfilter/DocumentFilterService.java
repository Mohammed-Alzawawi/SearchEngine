package com.example.SearchEngine.utils.documentfilter;

import com.example.SearchEngine.invertedindex.utils.CollectionInfo;
import com.example.SearchEngine.utils.documentfilter.matchfilter.MatchFilterService;
import com.example.SearchEngine.utils.documentfilter.rangefilter.RangeFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DocumentFilterService {

    @Autowired
    private RangeFilterService rangeFilterService;
    @Autowired
    private MatchFilterService matchFilterService;
    @Autowired
    private Merger merger;

    public List<Long> getDocuments(String schemaName, HashMap<String, Object> filters) throws Exception {
        HashMap<String, HashMap<String, Object>> ranges = (HashMap<String, HashMap<String, Object>>) filters.getOrDefault("range", new HashMap<>());
        HashMap<String, String> matches = (HashMap<String, String>) filters.getOrDefault("match", new HashMap<>());
        List<Long> filteredDocuments = CollectionInfo.getAllSchemaDocuments(schemaName);
        filteredDocuments = merger.mergeTwoLists(rangeFilterService.getFilteredDocuments(schemaName, ranges), filteredDocuments);
        filteredDocuments = merger.mergeTwoLists(matchFilterService.getFilteredDocuments(schemaName, matches), filteredDocuments);
        return filteredDocuments;
    }
}