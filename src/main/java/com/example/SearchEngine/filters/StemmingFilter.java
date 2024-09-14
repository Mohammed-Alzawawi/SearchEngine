package com.example.SearchEngine.filters;

import com.example.SearchEngine.tokenization.Token;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.util.List;

public class StemmingFilter implements Filter {
    private static final SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

    public StemmingFilter() {
    }

    @Override
    public List<Token> filter(Token token) {
        token.setWord(stemmer.stem(token.getWord()).toString());
        return List.of(token);
    }

}
