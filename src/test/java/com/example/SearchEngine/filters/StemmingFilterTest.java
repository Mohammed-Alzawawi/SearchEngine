package com.example.SearchEngine.filters;

import com.example.SearchEngine.tokenization.Token;
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
