package com.example.SearchEngine.Analyzers;

import com.example.SearchEngine.Filters.Filter;
import com.example.SearchEngine.Tokenization.Token;
import com.example.SearchEngine.Tokenization.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnalyzer extends Analyzer {

    public SimpleAnalyzer(Tokenizer tokenizer, List<Filter> filters) {
        this.tokenizer = tokenizer;
        this.filters = filters;
    }

    @Override
    public List<Token> analyze(String text, Double weight) {
        List<Token> tokens = tokenizer.tokenize(text, weight);

        for (Filter filter : filters) {
            List<Token> current = new ArrayList<>();
            for (Token token : tokens) {
                try {
                    filter.filter(token).stream().forEach(filterdToken -> current.add(filterdToken));
                } catch (Exception e) {

                }
            }
            tokens = current;
        }
        return tokens;
    }
}
