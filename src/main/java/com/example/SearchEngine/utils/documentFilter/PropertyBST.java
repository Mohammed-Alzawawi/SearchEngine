package com.example.SearchEngine.utils.documentFilter;

public class PropertyBST {
    private BSTNode root;

    public PropertyBST() {
        this.root = new BSTNode(null, null);
    }

    private BSTNode buildWithNewDocument(BSTNode curNode, Long documentID, Long value) {
        if (value >= curNode.getValue()) {
            if (curNode.getRight() == null) {
                curNode.setRight(new BSTNode(documentID, value));
                return curNode;
            }
            BSTNode newRight = buildWithNewDocument(curNode.getRight(), documentID, value);
            curNode.setRight(newRight);
        } else {
            if (curNode.getLeft() == null) {
                curNode.setLeft(new BSTNode(documentID, value));
                return curNode;
            }
            BSTNode newLeft = buildWithNewDocument(curNode.getLeft(), documentID, value);
            curNode.setLeft(newLeft);
        }
        return curNode;
    }

    public void addDocument(Long documentID, Long value) {
        if (root.getDocumentID() == null) {
            root = new BSTNode(documentID, value);
        } else {
            root = buildWithNewDocument(root, documentID, value);
        }
    }
}
