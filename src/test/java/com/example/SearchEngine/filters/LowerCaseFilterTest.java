package com.example.SearchEngine.filters;

import com.example.SearchEngine.tokenization.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class LowerCaseFilterTest {

    LowerCaseFilter lowerCaseFilter = new LowerCaseFilter();

    @Test
    void testLowerCaseFilter() {
        Token token = new Token("ANYThing", .5, 0);
        lowerCaseFilter.filter(token).stream().forEach(filterdToken -> Assertions.assertEquals(token.getWord().toLowerCase(), filterdToken.getWord()));
    }

}
