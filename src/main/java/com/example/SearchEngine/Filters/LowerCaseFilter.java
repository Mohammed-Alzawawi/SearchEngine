package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;

import java.util.List;

public class LowerCaseFilter implements Filter{
    public LowerCaseFilter() {
    }

    @Override
    public List<Token> filter(Token token) {
        token.setWord(token.getWord().toLowerCase());
        return List.of( token ) ;
    }
}
