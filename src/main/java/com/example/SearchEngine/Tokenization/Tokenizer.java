package com.example.SearchEngine.Tokenization;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public Tokenizer () {}
    public List<Token> tokenize (String text) {
        String words[] = text.split("\\s+") ;
        List<Token> tokens = new ArrayList<>() ;
        for (String word : words) {
            Token token = new Token(word) ;
            token = new LowerCaseToken(token) ;
            tokens.add(token) ;
        }
        return  tokens ;
    }

}
