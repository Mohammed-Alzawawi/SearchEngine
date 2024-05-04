package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;

import java.util.List;

public interface Filter {

    List<Token> filter(Token token);
}
