package com.example.SearchEngine.tokenization;

public class Token {
    private String word;
    private Double weight;
    private Integer position;

    public Token(String word, Double weight, Integer position) {
        this.word = word;
        this.weight = weight;
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Token{" + "word='" + word + '\'' + ", weight=" + weight + ", position=" + position + '}';
    }
}
