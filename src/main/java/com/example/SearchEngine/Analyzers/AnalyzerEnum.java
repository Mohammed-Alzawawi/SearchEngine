package com.example.SearchEngine.Analyzers;

import com.example.SearchEngine.Filters.LowerCaseFilter;
import com.example.SearchEngine.Filters.StemmingFilter;
import com.example.SearchEngine.Filters.StopWordsFilter;
import com.example.SearchEngine.Tokenization.SimpleTokenizer;

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
