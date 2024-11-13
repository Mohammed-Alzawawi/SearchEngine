package com.example.SearchEngine.utils.documentFilter.converter;

public class StringSize implements ToSizeInterface{
    public Long getSize(Object object) {
        return (long) ((String) object).length();
    }
}
