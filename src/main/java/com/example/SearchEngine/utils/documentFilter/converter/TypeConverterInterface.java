package com.example.SearchEngine.utils.documentFilter.converter;

import java.lang.reflect.InvocationTargetException;

public interface TypeConverterInterface {
    public Long convert(String type, Object object) throws InvocationTargetException, IllegalAccessException;
}
