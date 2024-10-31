package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StopWordsFilterTest {
    StopWordsFilter stopWordsFilter = new StopWordsFilter();

    @Test
    void testNonStopWordToken() {
        Token token = new Token("anyWord", .5, 0);
        Assertions.assertNotEquals(null, stopWordsFilter.filter(token));
    }

    @Test
    void testStopWordToken() {
        Token token = new Token("are", .5, 0);
        Assertions.assertNull(stopWordsFilter.filter(token));
    }

}
