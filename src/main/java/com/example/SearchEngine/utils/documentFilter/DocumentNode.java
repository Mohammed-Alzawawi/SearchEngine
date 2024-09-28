package com.example.SearchEngine.utils.documentFilter;

public class DocumentNode implements Comparable<DocumentNode> {

    private Long first;
    private Long second;

    public DocumentNode() {
    }

    public DocumentNode(Long first, Long second) {
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
    public int compareTo(DocumentNode other) {
        return Long.compare(this.getFirst(), other.getFirst());
    }
}
