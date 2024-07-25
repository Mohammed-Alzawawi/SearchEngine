package com.example.SearchEngine.schema.log;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class Tuple implements Comparable<Tuple> {
    private Long commandId;
    private Command command;
    private String documentId;

    public Tuple(Long commandId, Command command, String documentId) {
        this.commandId = commandId;
        this.command = command;
        this.documentId = documentId;
    }

    @Override
    public int compareTo(Tuple other) {
        return this.commandId.compareTo(other.commandId);
    }

    @Override
    public String toString() {
        return command + " " + documentId;
    }
}