package com.example.SearchEngine.tokenization;

import java.util.ArrayList;
import java.util.List;

public class SimpleTokenizer implements Tokenizer {

    public SimpleTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text, Double weight) {
        List<Token> tokens = new ArrayList<>();
        int index = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (Character.isDigit(text.charAt(i)) || Character.isLetter(text.charAt(i))) {
                current.append(text.charAt(i));
            } else {
                if (current.length() > 2) {
                    tokens.add(new Token(current.toString(), weight, index));
                }
                index = i + 1;
                current.setLength(0);
            }
        }

        if (current.length() > 2) {
            tokens.add(new Token(current.toString(), weight, index));
        }

        return tokens;
    }
}
