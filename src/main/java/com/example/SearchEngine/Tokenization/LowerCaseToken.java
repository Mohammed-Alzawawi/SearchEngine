package com.example.SearchEngine.Tokenization;

public class LowerCaseToken extends Token {
    Token token ;
    public LowerCaseToken (Token token) {
        this.token = token ;
    }
    public String getWord() {
        return token.getWord().toLowerCase() ;
    }


}
