package com.example.SearchEngine.invertedindex.utils;

import com.example.SearchEngine.analyzers.Analyzer;
import com.example.SearchEngine.analyzers.AnalyzerEnum;

public class SchemaAnalyzer {

    public  static  Analyzer getAnalyzer(String schemaName) {
        return AnalyzerEnum.EnglishAnalyzer.getAnalyzer();
    }
}
