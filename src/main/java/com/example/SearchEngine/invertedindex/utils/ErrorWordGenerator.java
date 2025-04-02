package com.example.SearchEngine.invertedindex.utils;

import java.util.ArrayList;
import java.util.List;

public class ErrorWordGenerator {

    private static void findAllError(int index, String word, StringBuilder current, List<String> errors, int counter) {
        if (index == word.length()) {
            if (counter > 0) {
                for (int i = 0; i < 26; i++) {
                    current.append((char) ('a' + i));
                    errors.add(current.toString());
                    current.deleteCharAt(current.length() - 1);
                }
            }
            errors.add(current.toString());
            return;
        }
        current.append(word.charAt(index));
        findAllError(index + 1, word, current, errors, counter);
        current.deleteCharAt(current.length() - 1);

        if (counter > 0) {
            for (int i = 0; i < 26; i++) {
                current.append((char) ('a' + i));
                findAllError(index, word, current, errors, counter - 1);
                current.deleteCharAt(current.length() - 1);

                if (word.charAt(index) != (char) ('a' + i)) {
                    current.append((char) ('a' + i));
                    findAllError(index + 1, word, current, errors, counter - 1);
                    current.deleteCharAt(current.length() - 1);
                }
            }
            findAllError(index + 1, word, current, errors, counter - 1);
        }
    }

    public static List<String> generate(String word, int numberOfError) {
        List<String> errors = new ArrayList<String>();
        findAllError(0, word, new StringBuilder(), errors, numberOfError);
        return errors;
    }
}
