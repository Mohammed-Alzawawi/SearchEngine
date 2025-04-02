package com.example.SearchEngine.filters;

import com.example.SearchEngine.tokenization.Token;

import java.util.List;

public interface Filter {

    List<Token> filter(Token token);
}
