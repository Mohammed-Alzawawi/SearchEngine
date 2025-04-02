package com.example.SearchEngine.tokenization;

import java.util.ArrayList;
import java.util.List;

public class WhiteSpaceTokenizer implements Tokenizer {

    public WhiteSpaceTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text, Double weight) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int index = 0;

        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                if (current.length() > 2) {
                    tokens.add(new Token(current.toString(), weight, index));
                }
                index = i + 1;
                current.setLength(0);
            } else {
                current.append(text.charAt(i));
            }
        }

        if (current.length() > 2) {
            tokens.add(new Token(current.toString(), weight, index));
        }
        return tokens;
    }
}
