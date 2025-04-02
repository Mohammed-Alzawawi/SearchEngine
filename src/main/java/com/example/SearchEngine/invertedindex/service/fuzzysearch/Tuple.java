package com.example.SearchEngine.invertedindex.service.fuzzysearch;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Tuple implements Comparable<Tuple> {

    private String word;
    private Integer distance;

    @Override
    public int compareTo(Tuple other) {
        return Integer.compare(this.distance, other.distance);
    }
}
