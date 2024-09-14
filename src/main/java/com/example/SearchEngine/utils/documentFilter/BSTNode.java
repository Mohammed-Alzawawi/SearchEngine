package com.example.SearchEngine.utils.documentFilter;

import java.io.Serializable;

public class BSTNode implements Serializable {
    private Long documentID;
    private Long value;
    private BSTNode left;
    private BSTNode right;

    public BSTNode(Long documentID, Long value) {
        this.documentID = documentID;
        this.value = value;
    }

    public Long getDocumentID() {
        return documentID;
    }

    public void setDocumentID(Long documentID) {
        this.documentID = documentID;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public BSTNode getLeft() {
        return left;
    }

    public void setLeft(BSTNode left) {
        this.left = left;
    }

    public BSTNode getRight() {
        return right;
    }

    public void setRight(BSTNode right) {
        this.right = right;
    }
}
