package com.example.SearchEngine.analyzers;

import com.example.SearchEngine.filters.Filter;
import com.example.SearchEngine.tokenization.Token;
import com.example.SearchEngine.tokenization.Tokenizer;

import java.util.List;

public abstract class Analyzer {

    public Tokenizer tokenizer;
    public List<Filter> filters;

    public abstract List<Token> analyze(String text, Double weight);
}
