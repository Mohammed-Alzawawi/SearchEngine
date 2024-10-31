package com.example.SearchEngine.invertedIndex.service.fuzzySearch;

import com.example.SearchEngine.Analyzers.AnalyzerEnum;
import com.example.SearchEngine.Tokenization.Token;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FuzzyTrie {

    private final int NumberOfError = 1;
    private final int NumberOfSimilar = 2;

    private void findAllError(int index, String word, StringBuilder current, List<String> errors, int counter) {
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

    private FuzzyNode getWordLastNode(FuzzyNode root, String word) {
        FuzzyNode currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            currentNode = currentNode.getNextNode(c);
        }
        return currentNode;
    }

    private boolean checkWordExist(FuzzyNode root, String word) {
        FuzzyNode currentNode = root;
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            if (!currentNode.hasNextNode(c)) {
                return false;
            }
            currentNode = currentNode.getNextNode(c);
        }
        return true;
    }

    private void addWord(String error, String word, FuzzyNode root) {
        FuzzyNode lastNode = getWordLastNode(root, error);
        lastNode.setEndOfTerm(true);
        lastNode.addWord(word);
    }

    private void removeWord(String error, String word, FuzzyNode root) {
        if (checkWordExist(root, error)) {
            FuzzyNode lastNode = getWordLastNode(root, error);
            lastNode.removeWord(word);
        }
    }

    private int editDistance(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    public void addField(String fieldData, String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Token> tokens = AnalyzerEnum.DefaultAnalyzer.getAnalyzer().analyze(fieldData, 1.0);
        for (Token token : tokens) {
            List<String> errors = new ArrayList<>();
            findAllError(0, token.getWord(), new StringBuilder(), errors, NumberOfError);
            for (String error : errors) {
                addWord(error, token.getWord(), root);
            }
        }
    }

    public void removeField(String fieldData, String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Token> tokens = AnalyzerEnum.DefaultAnalyzer.getAnalyzer().analyze(fieldData, 1.0);
        for (Token token : tokens) {
            List<String> errors = new ArrayList<>();
            findAllError(0, token.getWord(), new StringBuilder(), errors, NumberOfError);
            for (String error : errors) {
                removeWord(error, token.getWord(), root);
            }
        }
    }

    public List<String> findMostSimilarWord(String word , String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Tuple> words = new ArrayList<>();
        List<String> errors = new ArrayList<>() , similarWords = new ArrayList<>() ;

        findAllError(0, word, new StringBuilder(), errors, NumberOfError);
        for (String error : errors) {
            if (checkWordExist(root, error)) {
                FuzzyNode node = getWordLastNode(root, error) ;
                for (String term : node.getWordsFrequency().keySet() ) {
                    words.add(new Tuple(term, editDistance(word, term)));
                }
            }
        }
        Collections.sort(words);
        for (int i = 0 ; i < Math.min(words.size() , NumberOfSimilar) ; i++ ) {
            similarWords.add(words.get(i).getWord()) ;
            if (words.get(i).getDistance() == 0 ) {
                break;
            }
        }
        return similarWords;
    }
}
