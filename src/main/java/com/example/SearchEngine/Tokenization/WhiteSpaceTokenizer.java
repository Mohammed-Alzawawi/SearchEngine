package com.example.SearchEngine.Tokenization;

import java.util.ArrayList;
import java.util.List;

public class WhiteSpaceTokenizer implements Tokenizer{

    public WhiteSpaceTokenizer() {}

    @Override
    public List<Token> tokenize(String text , Double weight ) {
        List<Token> tokens = new ArrayList<>() ;
        String cur ="" ;
        int index = 0 ;

        for (int i = 0 ; i < text.length() ; i++ ) {
            if ( Character.isWhitespace(text.charAt(i)) ) {
                if (cur.length() > 2 )
                    tokens.add(new Token(cur , weight , index)) ;
                index = i +1 ;
                cur = "" ;
            } else {
                cur +=text.charAt(i) ;
            }
        }

        if (cur.length() >  2)
            tokens.add(new Token(cur , weight , index)) ;
        return  tokens ;
    }
}
