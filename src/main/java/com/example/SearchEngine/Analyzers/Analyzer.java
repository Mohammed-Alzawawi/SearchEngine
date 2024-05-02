package com.example.SearchEngine.Analyzers;

import com.example.SearchEngine.Filters.Filter;
import com.example.SearchEngine.Tokenization.Token;

import java.util.List;

public interface Analyzer {
    public List<Token> analyze(List<Token> tokens , List<Filter> filters) ;
}
