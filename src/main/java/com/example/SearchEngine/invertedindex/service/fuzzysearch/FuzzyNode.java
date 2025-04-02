package com.example.SearchEngine.invertedindex.service.fuzzysearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class FuzzyNode implements Serializable {
    private static final long serialVersionUID = 5424601767160570951L;
    private boolean endOfTerm = false;
    private HashMap<Character, FuzzyNode> nextNodes = new HashMap<>();
    private HashMap<String, Integer> wordsFrequency = new HashMap<>();

    public FuzzyNode getNextNode(Character c) {
        if (hasNextNode(c)) {
            return nextNodes.get(c);
        }
        nextNodes.put(c, new FuzzyNode());
        return nextNodes.get(c);
    }

    public boolean hasNextNode(Character c) {
        return nextNodes.containsKey(c);
    }

    public void addWord(String word) {
        if (!wordsFrequency.containsKey(word)) {
            wordsFrequency.put(word, 0);
        }
        wordsFrequency.put(word, wordsFrequency.get(word) + 1);
    }

    public void removeWord(String word) {
        if (wordsFrequency.containsKey(word)) {
            wordsFrequency.put(word, wordsFrequency.get(word) - 1);
            if (wordsFrequency.get(word) == 0) {
                wordsFrequency.remove(word);
            }
        }
        if (wordsFrequency.isEmpty()){
            endOfTerm = false;
        }
    }
}
