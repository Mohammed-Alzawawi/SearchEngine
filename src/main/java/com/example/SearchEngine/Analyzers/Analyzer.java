package com.example.SearchEngine.Analyzers;

import com.example.SearchEngine.Filters.Filter;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.Tokenization.Tokenizer;

import java.util.List;

public abstract class Analyzer {

    public Tokenizer tokenizer;
    public List<Filter> filters;

    public abstract List<Token> analyze(String text, Double weight);
}
