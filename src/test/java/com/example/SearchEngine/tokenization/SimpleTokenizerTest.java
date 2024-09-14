package com.example.SearchEngine.tokenization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleTokenizerTest {
    SimpleTokenizer simpleTokenizer = new SimpleTokenizer();

    @Test
    void testSimpleTokenizer() {
        String testString = "hello , this is test for simple_Tokenizer";
        String expected = "hello this test for simple Tokenizer";
        StringBuilder actual = new StringBuilder();
        simpleTokenizer.tokenize(testString, .05).stream().forEach(token -> actual.append((actual.length() > 0 ? " " + token.getWord() : token.getWord())));
        Assertions.assertEquals(expected, actual.toString());
    }

}
