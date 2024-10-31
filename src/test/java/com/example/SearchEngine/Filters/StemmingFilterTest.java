package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StemmingFilterTest {

    StemmingFilter stemmingFilter = new StemmingFilter();

    @Test
    void testStemmingFilter() {
        Token token = new Token("working", .5, 0);
        stemmingFilter.filter(token).stream().forEach(filterdToken -> Assertions.assertEquals("work", filterdToken.getWord()));
    }


}
