package com.example.SearchEngine.Analyzers;

import com.example.SearchEngine.Filters.Filter;
import com.example.SearchEngine.Tokenization.Token;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnalyzer implements Analyzer {

    public  SimpleAnalyzer(){} ;

    @Override
    public List<Token> analyze(List<Token> tokens, List<Filter> filters) {
        for(Filter filter : filters) {
            List<Token> cur = new ArrayList<>() ;
            for (Token token : tokens) {
               filter.filter(token).stream().forEach(i -> cur.add(i));
            }
            tokens = cur ;
        }
        return  tokens;
    }
}
