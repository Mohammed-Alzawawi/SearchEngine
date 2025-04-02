package com.example.SearchEngine.Analyzer;

import com.example.SearchEngine.analyzers.SimpleAnalyzer;
import com.example.SearchEngine.filters.LowerCaseFilter;
import com.example.SearchEngine.filters.StemmingFilter;
import com.example.SearchEngine.filters.StopWordsFilter;
import com.example.SearchEngine.tokenization.SimpleTokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SimpleAnalyzerTest {
    SimpleAnalyzer analyzer = new SimpleAnalyzer(new SimpleTokenizer(), List.of(new LowerCaseFilter(), new StopWordsFilter(), new StemmingFilter()));

    @Test
    void testSimpleAnalyzer() {
        String testString = "HELLO , this is a test for Simple__ANAlyzer ";
        String expected = "hello test simpl analyz";
        StringBuilder actual = new StringBuilder();
        analyzer.analyze(testString, .05).stream().forEach(token -> actual.append((actual.length() > 0 ? " " + token.getWord() : token.getWord())));
        Assertions.assertEquals(expected, actual.toString());
    }


}
