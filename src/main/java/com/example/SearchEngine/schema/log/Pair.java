package com.example.SearchEngine.schema.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair {
    public Long first;
    public Command second;

    public Pair(Long first, Command second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return this.first.toString() + " " + this.second.toString();
    }
}