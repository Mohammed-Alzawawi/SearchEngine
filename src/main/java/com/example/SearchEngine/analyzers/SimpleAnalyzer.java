package com.example.SearchEngine.analyzers;

import com.example.SearchEngine.filters.Filter;
import com.example.SearchEngine.tokenization.Token;
import com.example.SearchEngine.tokenization.Tokenizer;

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
                List<Token> filteredTokens = filter.filter(token) ;
                if (filteredTokens!=null) {
                    filteredTokens.stream().forEach(filterdToken->current.add(filterdToken));
                }
            }
            tokens = current;
        }
        return tokens;
    }
}
