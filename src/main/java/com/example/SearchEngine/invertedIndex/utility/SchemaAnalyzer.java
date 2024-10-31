package com.example.SearchEngine.invertedIndex.utility;

import com.example.SearchEngine.Analyzers.Analyzer;
import com.example.SearchEngine.Analyzers.AnalyzerEnum;

public class SchemaAnalyzer {

    public  static  Analyzer getAnalyzer(String schemaName) {
        return AnalyzerEnum.EnglishAnalyzer.getAnalyzer();
    }
}
