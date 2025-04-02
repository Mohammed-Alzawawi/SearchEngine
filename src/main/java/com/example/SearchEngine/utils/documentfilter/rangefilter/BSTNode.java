package com.example.SearchEngine.utils.documentfilter.rangefilter;

import java.io.Serializable;

public class BSTNode implements Comparable<BSTNode>, Serializable {
    private static final long serialVersionUID = 3959359313312146859L;
    private Long first;
    private Long second;

    public BSTNode() {
    }

    public BSTNode(Long first, Long second) {
        this.first = first;
        this.second = second;
    }

    public Long getFirst() {
        return first;
    }

    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getSecond() {
        return second;
    }

    public void setSecond(Long second) {
        this.second = second;
    }

    @Override
    public int compareTo(BSTNode other) {
        if (Long.compare(this.getFirst(), other.getFirst()) != 0) {
            return Long.compare(this.getFirst(), other.getFirst());
        }
        return Long.compare(this.getSecond(), other.getSecond());
    }

    @Override
    public String toString() {
        return "DocumentNode{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
