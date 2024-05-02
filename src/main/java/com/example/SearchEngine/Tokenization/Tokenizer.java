package com.example.SearchEngine.Tokenization;

import java.util.List;

public interface Tokenizer {
    public List<Token> tokenize(String text , Double weight) ;
}
