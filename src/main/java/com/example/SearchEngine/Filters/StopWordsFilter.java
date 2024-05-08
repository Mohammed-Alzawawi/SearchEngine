package com.example.SearchEngine.Filters;

import com.example.SearchEngine.Tokenization.Token;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class StopWordsFilter implements Filter {
    private static HashSet<String> stopwWords = new HashSet<>();

    public StopWordsFilter() {
        if (stopwWords.isEmpty()) {
            Resource resource = new ClassPathResource("stopWords.txt");
            try (InputStream inputStream = resource.getInputStream()) {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNext()) stopwWords.add(scanner.next());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Token> filter(Token token) {
        if (stopwWords.contains(token.getWord())) return null;
        return List.of(token);
    }
}
