package com.example.SearchEngine.utils.documentfilter.converter;

public class StringSize implements ToSizeInterface{
    public Long getSize(Object object) {
        return (long) ((String) object).length();
    }
}
