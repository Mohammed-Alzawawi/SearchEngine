package com.example.SearchEngine.utils.documentFilter.converter;

import java.util.List;

public class ListSize implements ToSizeInterface{
    public Long getSize(Object object) {
        return (long) ((List) object).size();
    }
}
