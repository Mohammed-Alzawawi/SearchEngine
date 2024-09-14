package com.example.SearchEngine.analyzers;

import com.example.SearchEngine.filters.LowerCaseFilter;
import com.example.SearchEngine.filters.StemmingFilter;
import com.example.SearchEngine.filters.StopWordsFilter;
import com.example.SearchEngine.tokenization.SimpleTokenizer;

import java.util.List;

public enum AnalyzerEnum {

    EnglishAnalyzer(new SimpleAnalyzer(new SimpleTokenizer(), List.of(new StopWordsFilter(), new StemmingFilter(), new LowerCaseFilter())));

    private final SimpleAnalyzer analyzer;

    AnalyzerEnum(SimpleAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public SimpleAnalyzer getAnalyzer() {
        return analyzer;
    }
}
