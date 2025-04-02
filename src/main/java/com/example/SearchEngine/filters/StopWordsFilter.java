package com.example.SearchEngine.filters;

import com.example.SearchEngine.tokenization.Token;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class StopWordsFilter implements Filter {
    private static HashSet<String> stopWords = new HashSet<>();

    public StopWordsFilter() {
        if (stopWords.isEmpty()) {
            Resource resource = new ClassPathResource("stopWords.txt");
            try (InputStream inputStream = resource.getInputStream()) {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNext()) stopWords.add(scanner.next());
            } catch (IOException e) {
                throw new IllegalStateException() ;
            }
        }
    }

    @Override
    public List<Token> filter(Token token) {
        if (stopWords.contains(token.getWord())) return null;
        return List.of(token);
    }
}
