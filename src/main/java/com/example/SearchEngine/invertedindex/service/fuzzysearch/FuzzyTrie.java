package com.example.SearchEngine.invertedindex.service.fuzzysearch;

import com.example.SearchEngine.analyzers.AnalyzerEnum;
import com.example.SearchEngine.tokenization.Token;
import com.example.SearchEngine.invertedindex.utils.EditDistance;
import com.example.SearchEngine.invertedindex.utils.ErrorWordGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FuzzyTrie {

    private final int NumberOfError = 1;
    private final int NumberOfSimilar = 2;


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


    public void addField(String fieldData, String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Token> tokens = AnalyzerEnum.DefaultAnalyzer.getAnalyzer().analyze(fieldData, 1.0);
        for (Token token : tokens) {
            List<String> errors = ErrorWordGenerator.generate(token.getWord() , NumberOfError);
            for (String error : errors) {
                addWord(error, token.getWord(), root);
            }
        }
    }

    public void removeField(String fieldData, String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Token> tokens = AnalyzerEnum.DefaultAnalyzer.getAnalyzer().analyze(fieldData, 1.0);
        for (Token token : tokens) {
            List<String> errors = ErrorWordGenerator.generate(token.getWord() , NumberOfError);
            for (String error : errors) {
                removeWord(error, token.getWord(), root);
            }
        }
    }

    public List<String> findMostSimilarWord(String word , String schemaName) {
        FuzzyNode root = FuzzyRoot.getRoot(schemaName);
        List<Tuple> words = new ArrayList<>();
        List<String> errors = ErrorWordGenerator.generate(word , NumberOfError), similarWords = new ArrayList<>() ;
        for (String error : errors) {
            if (checkWordExist(root, error)) {
                FuzzyNode node = getWordLastNode(root, error) ;
                for (String term : node.getWordsFrequency().keySet() ) {
                    words.add(new Tuple(term, EditDistance.editDistance(word, term)));
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
