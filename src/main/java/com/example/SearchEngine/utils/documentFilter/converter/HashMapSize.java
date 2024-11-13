package com.example.SearchEngine.utils.documentFilter.converter;

import java.util.HashMap;

public class HashMapSize implements ToSizeInterface{
    public Long getSize(Object object) {
        return (long) ((HashMap) object).size();
    }
}
