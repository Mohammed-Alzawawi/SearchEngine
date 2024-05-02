package com.example.SearchEngine.Tokenization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SimpleTokenizer implements  Tokenizer {

    public SimpleTokenizer() {}

    @Override
    public List<Token> tokenize(String text, Double weight) {
        List<Token> tokens = new ArrayList<>() ;
        int index = 0 ;
        String cur = "" ;

        for (int i = 0 ; i < text.length() ; i++ ) {
            if (Character.isDigit(text.charAt(i)) || Character.isLetter(text.charAt(i))) {
                cur+=text.charAt(i) ;
            } else {
                if (cur.length() > 2 )
                    tokens.add(new Token(cur , weight , index)) ;
                index = i +1 ;
                cur = "" ;
            }
        }

        if (cur.length() >  2)
            tokens.add(new Token(cur , weight , index)) ;

        return  tokens ;
    }
}
