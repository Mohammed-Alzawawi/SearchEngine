package com.example.SearchEngine.tokenization;

import java.util.List;

public interface Tokenizer {
    List<Token> tokenize(String text, Double weight);
}
