package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;

import java.util.List;

public class DuplicateFilter implements Filter {

    public DuplicateFilter() {
    }

    @Override
    public List<Token> filter(Token token) {
        return List.of(token, token);
    }
}
