package com.example.SearchEngine.tokenization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class WhiteSpaceTokenizerTest {
    WhiteSpaceTokenizer whiteSpaceTokenizer = new WhiteSpaceTokenizer();

    @Test
    void testWhiteSpaceTokenizer() {
        String testString = "hello this is test for white space tokenizer";
        String expected = "hello this test for white space tokenizer";
        StringBuilder actual = new StringBuilder();
        whiteSpaceTokenizer.tokenize(testString, .05).stream().forEach(token -> actual.append((actual.length() > 0 ? " " + token.getWord() : token.getWord())));
        Assertions.assertEquals(expected, actual.toString());
    }

}
